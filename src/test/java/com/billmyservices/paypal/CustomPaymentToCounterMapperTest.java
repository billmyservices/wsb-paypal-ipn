package com.billmyservices.paypal;

import com.billmyservices.paypal.ipn.CounterAction;
import com.paypal.ipn.IPNMessage;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomPaymentToCounterMapperTest {

    private static String rndString() {
        return UUID.randomUUID().toString();
    }

    private static int rndInt(int minIncluded, int maxExcluded) {
        return ThreadLocalRandom.current().nextInt(minIncluded, maxExcluded);
    }

    @Test
    public void mapPayment() {

        // Some random values
        final int quantity = rndInt(1, 10);
        final int units = rndInt(1, 30);
        final String payer_email = String.format("%s@%s.%s", rndString(), rndString(), rndString());
        final String counterTypeCode = rndString();
        final String counterCode = CustomPaymentToCounterMapper.secureUserId(payer_email);

        // Minimum expected Paypal data
        final Map<String, String[]> ipnMap = new HashMap<>();
        ipnMap.put("payer_email", new String[]{payer_email});
        ipnMap.put("item_number", new String[]{counterTypeCode});
        ipnMap.put("quantity", new String[]{Integer.toString(quantity)});
        ipnMap.put("option_selection" + Integer.toString(rndInt(1, 5)), new String[]{String.format("%d units", units)});

        // Simulated process
        final CounterAction counterAction = new CustomPaymentToCounterMapper().mapPayment(new IPNMessage(ipnMap, true));

        // Expected result
        assertNotNull("The counter action cannot be null", counterAction);
        assertEquals("The counter type code is not the expected one", counterTypeCode, counterAction.getCounterTypeCode());
        assertEquals("The counter code is not the expected one", counterCode, counterAction.getCounterCode());
        assertEquals("The counter value to inc is not the expected one", (long) quantity * units, (long) counterAction.getValueToAdd());

    }
}