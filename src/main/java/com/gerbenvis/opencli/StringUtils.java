package com.gerbenvis.opencli;

public final class StringUtils {

    public static String stripLatest(final String value) {
        if (value != null && value.length() >= 1) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
