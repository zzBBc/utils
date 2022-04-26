package com.github.zzbbc.utils;

public class NumberUtils {
    public static Integer toInteger(String value) {
        if (!StringUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        }

        return null;
    }
}
