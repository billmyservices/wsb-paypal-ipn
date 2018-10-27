package com.billmyservices.paypal;

import com.billmyservices.paypal.ipn.Cfg;
import com.billmyservices.paypal.ipn.CounterAction;
import com.billmyservices.paypal.ipn.Log;
import com.paypal.ipn.IPNMessage;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.billmyservices.paypal.ipn.Cfg.parseInteger;
import static java.util.Optional.empty;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.joining;

/**
 * <p>This class contains the unique required customization to manage
 * each Paypal payment result and update (create, reset, ...) the
 * customer Bill My Services counters.</p>
 *
 * <p>In this example, we use the bellow information to map the payment:</p>
 *
 * <ul>
 * <li><code>Paypal.item_number := BillMyServices.counterType</code>, when configure your Paypal payment process
 *  (e.g. one button), you must set as <code>item_number</code> one valid BillMyServices <code>counter type</code>
 *  code. Usually that counter type define your selling item (e.g. "the user can do login on my app").</li>
 *
 * <li><code>Paypal.quantity</code>, when configure your Paypal payment process your customers could set the quantity to buy.</li>
 *
 * <li><code>Paypal.option_selection#</code>, <b>IMPORTANT</b> this IPN listener expect <code>option_selection#</code> follow
 * exactly the <b>##### units</b> where <b>#####</b> will be the units bought with that option. This allow you
 * to set best prices when the customer buy more units (e.g. "10 units ~ 1$", "100 units ~ 9$", ...).</li>
 *
 * <li><code>Paypal.payer_email := BillMyServices.counter</code>, as <code>counter type</code> is a general (for all customers)
 * code, here we use the <code>payer_email</code> to encode the customer <code>counter</code> code on Bill My Services.
 * We do not need store that <code>payer_email</code>, in its place, we perform a secure hash over it.</li>
 * </ul>
 *
 * <p>Then, this IPN listener will map each success Paypal payment process as follow:</p>
 *
 * <p><i>"The Bill My Service counter identified by the <code>payer_email</code> of type identified by the <code>item_number</code>,
 * will be increased as the product of <code>quantity</code> and <code>option_selection</code>."</i></p>
 *
 *
 */
public class CustomPaymentToCounterMapper {

    /**
     * This method map the Paypal payment information into Bill My Services counter actions.
     * You can/should customize this business logic to your specific needs.
     *
     * @param paypalMessage the Paypal payment information
     * @return the Bill My Services actions to perform
     */
    public CounterAction mapPayment(IPNMessage paypalMessage) {

        // There is a lot of ways to map `counter-type`, `counter` and
        // recharge options. See https://www.billmyservices.com for
        // use cases.

        return new CounterAction()

                // The simplest way to map the `counter-type` is when it is
                // fixed for all customers (e.g. "My Report Views" where the
                // customer buy the number of times he will can access to the
                // "My Report Views" functionality).
                .counterTypeCode(paypalMessage.getIpnMap().get("item_number"))


                // The simplest way to map the `counter` code is using the user email.
                // Following the previous example, one specific user can access
                // to the "My Report Views" many times as their counter value with
                // code that email. Since the user mail is a sensible data, you should
                // encrypt it, the simplest way is filter it using a cryptographic hash.
                .counterCode(secureUserId(paypalMessage.getIpnMap().get("payer_email")))

                // The simplest way to map the quantity to increment that counter
                // is use the quantity of purchased units.
                // If you want create discount packages, use options like "100 unds"
                .valueToAdd(lookupOption(paypalMessage)
                        .flatMap(units -> parseInteger(paypalMessage.getIpnMap().get("quantity")).map(quantity -> units * quantity))
                        .orElseThrow(() -> new IllegalStateException("paypal has been returned an unknown 'quantity' and/or 'selected_option#' values")));

    }




    private static final String OPTION_QTY_REGEX = "^([0-9]+) units$";

    // WARNING, this hashing protect the email, not the code, this encoding mechanism should be shared along your system.
    static String secureUserId(final String payer_email) {
        Objects.requireNonNull(payer_email);
        try {
            final MessageDigest digester = MessageDigest.getInstance("SHA-256");
            digester.update(payer_email.getBytes(Charset.forName("UTF-8")));
            return Base64.getEncoder().encodeToString(digester.digest());
        } catch (NoSuchAlgorithmException e) {
            // FATAL error application cannot work without SHA-256
            throw new IllegalStateException(e);
        }
    }

    // lookup options like "100 unds" (see OPTION_QTY_REGEX)
    static Optional<Integer> lookupOption(IPNMessage paypalMessage) {
        final Map<String, String> m = paypalMessage.getIpnMap();
        final List<String> optionKeys = m.keySet().stream().filter(k -> k.startsWith("option_selection")).collect(Collectors.toList());
        if (optionKeys.size() == 0)
            return empty();
        if (optionKeys.size() != 1) {
            Log.warn("the payment transaction (txn_id = %s) has multiple option selection (%s)", m.get("txn_id"), optionKeys.stream().collect(joining(", ")));
            return empty();
        }
        final Matcher mx = Pattern.compile(OPTION_QTY_REGEX, CASE_INSENSITIVE).matcher(m.get(optionKeys.get(0)));
        if (!mx.find()) {
            Log.warn("the payment transaction (txn_id = %s) has a bad option selection (%s)", m.get("txn_id"), optionKeys.get(0));
            return empty();
        }
        final Optional<Integer> maybeQty = Cfg.parseInteger(mx.group(1));
        if (!maybeQty.isPresent()) {
            Log.warn("the payment transaction (txn_id = %s), possible bug at CustomPaymentToCounterMapper.lookupOption", m.get("txn_id"));
            return empty();
        }
        return maybeQty;
    }

}
