package com.billmyservices.paypal.ipn;

import com.paypal.core.LoggingManager;

public class Log {
    public static void error(String format, Object... args) {
        LoggingManager.severe(App.class, String.format(format, args));
    }

    public static void warn(String format, Object... args) {
        LoggingManager.warn(App.class, String.format(format, args));
    }

    public static void info(String format, Object... args) {
        LoggingManager.info(App.class, String.format(format, args));
    }
}
