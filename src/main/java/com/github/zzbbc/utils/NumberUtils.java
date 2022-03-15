package com.github.zzbbc.utils;

public class NumberUtils {
    public static Integer toInteger(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();
        if (!value.equals("")) {
            return Integer.parseInt(value);
        }

        return null;
    }
}
