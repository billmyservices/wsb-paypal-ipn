package com.billmyservices.paypal.ipn;

import com.billmyservices.cli.BMSClient;
import com.billmyservices.cli.Result;
import com.billmyservices.paypal.CustomPaymentToCounterMapper;
import com.paypal.core.Constants;
import com.paypal.ipn.IPNMessage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.billmyservices.paypal.ipn.Log.*;

public class Digest extends HttpServlet {
    private final Map<String, String> config;
    private final CustomPaymentToCounterMapper paymentToCounterMapper = new CustomPaymentToCounterMapper();

    public Digest() {
        info("Digest HttpServlet start");
        this.config = new HashMap<>();
        config.put(Constants.MODE, Cfg.string("mode", Constants.SANDBOX));
        config.put("acct1.UserName", Cfg.string("username"));
        config.put("acct1.Password", Cfg.string("password"));
        info("wsb-paypal-ipn digest handler in mode '%s' and user '%s'", config.get(Constants.MODE), config.get("acct1.UserName"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String txn_id = "n/a";
        try {
            // Paypal sdk parse the payment request and validate it
            final IPNMessage ipnlistener = new IPNMessage(request, config);
            txn_id = ipnlistener.getIpnMap().get("txn_id");

            info("the payment transaction (txn_id = %s) starting mapping", txn_id);
            if (ipnlistener.validate()) {
                // WARNING now the payment could be lost!
                info("the payment transaction (txn_id = %s) has been validated", txn_id);

                // Map the payment to the counter action to perform
                final CounterAction ca = paymentToCounterMapper.mapPayment(ipnlistener);

                // Recharge Bill My Services counter
                final Result<Boolean> rs = BMSClient.getDefault().postCounter(ca.getCounterTypeCode(), ca.getCounterCode(), ca.getValueToAdd()).get();
                if (rs.isSuccess() && rs.get()) {
                    info("digest payment transaction (txn_id = %s) %s.%s += %d",
                            txn_id, ca.getCounterTypeCode(), ca.getCounterCode(), ca.getValueToAdd());
                } else {
                    error("cannot digest payment transaction (txn_id = %s) %s.%s += %d. Error: %s",
                            txn_id, ca.getCounterTypeCode(), ca.getCounterCode(), ca.getValueToAdd(), rs.getErrorMessage());
                }
            } else {
                warn("the payment transaction (txn_id = %s) is not validated", txn_id);
            }
        } catch (Exception e) {
            error("FATAL ERROR in doPost (txn_id = %s): %s", txn_id, e.getLocalizedMessage());
            throw new IllegalStateException(e);
        }
    }

}