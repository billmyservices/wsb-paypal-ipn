package com.billmyservices.paypal.ipn;

import java.util.Optional;

import static com.billmyservices.paypal.ipn.Log.warn;
import static java.util.Optional.*;

/**
 * Very simple configuration names.
 * <p>
 * Read values from system properties or else, from environment variables.
 * <p>
 * For certain "foo" configuration name, the system property is called "ipn.foo" and the environment variable "IPN_FOO".
 */
public final class Cfg {
    private static final String PROPERTY_PREFFIX = "ipn.";
    private static final String VARENV_PREFFIX = "IPN_";

    /**
     * Try to read some string from configuration.
     *
     * @param name the configuration name
     * @return maybe the configured value
     */
    public static Optional<String> maybeString(final String name) {
        final Optional<String> pv = ofNullable(System.getProperty(PROPERTY_PREFFIX + name.toLowerCase()));
        final Optional<String> ev = ofNullable(System.getenv(VARENV_PREFFIX + name.toUpperCase()));
        return pv.isPresent() ? pv : ev;
    }

    /**
     * Read some string from configuration or throw exception.
     *
     * @param name the configuration name
     * @return configured value.
     * @throws IllegalArgumentException if the value is not configured.
     */
    public static String string(final String name) {
        return maybeString(name).orElseThrow(() -> new IllegalArgumentException(String.format("wsb-payment-ipn require to configure the '%s' value", name)));
    }

    /**
     * Read some string configuration or return the default value.
     *
     * @param name   the configuration name
     * @param orelse value if not configured.
     * @return configured value or the default value.
     */
    public static String string(final String name, final String orelse) {
        return maybeString(name).orElse(orelse);
    }


    /**
     * Try to read some integer from configuration.
     *
     * @param name the configuration name
     * @return maybe the configured value
     */
    public static Optional<Integer> maybeInteger(final String name) {
        return maybeString(name).flatMap(Cfg::parseInteger);
    }

    /**
     * Try to parse an integer from configuration.
     *
     * @param xs the string containing an integer
     * @return maybe the parsed integer
     */
    public static Optional<Integer> parseInteger(String xs) {
        try {
            return of(Integer.parseInt(xs));
        } catch (NumberFormatException e) {
            warn("cannot parse '%s' as an integer number, error: %s", xs, e.getLocalizedMessage());
            return empty();
        }
    }

    /**
     * Read some integer from configuration or throw exception.
     *
     * @param name the configuration name
     * @return configured value.
     * @throws IllegalArgumentException if the value is not configured.
     */
    public static Integer integer(final String name) {
        return maybeInteger(name).orElseThrow(() -> new IllegalArgumentException(String.format("wsb-payment-ipn require to configure the '%s' number value", name)));
    }

    /**
     * Read some integer configuration or return the default value.
     *
     * @param name   the configuration name
     * @param orelse value if not configured.
     * @return configured value or the default value.
     */
    public static Integer integer(final String name, final int orelse) {
        return maybeInteger(name).orElse(orelse);
    }
}
