package com.github.zzbbc.utils;

import java.sql.Clob;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;

public class StringUtils {
    public static String toString(Clob clob) throws SQLException {
        if (clob != null) {
            return clob.getSubString(1, (int) clob.length());
        }

        return "";
    }

    public static String toString(Object object) {
        if (object == null) {
            return "";
        }

        return object.toString();
    }

    public String toString(Map<String, List<String>> formData) {
        StringBuilder builder = new StringBuilder("[");

        formData.forEach((key, value) -> {
            builder.append(key).append(": ").append(join(",", value));
        });

        builder.append("]");

        return builder.toString();
    }

    private static Entry<String, String[]> toEntryMap(String value, String regex) {
        Entry<String, String[]> entry = null;
        int firstIndex = value.indexOf(regex);

        String cmd = value.substring(0, firstIndex);

        String ref = value.substring(firstIndex + 1);
        String[] split = ref.split(",");

        entry = new SimpleEntry<>(cmd, split);

        return entry;
    }

    public static Map<String, String[]> toMap(String value, String regex) {
        Map<String, String[]> result = new HashMap<>();

        String[] groups = value.split(regex);
        for (String group : groups) {
            Entry<String, String[]> entry = toEntryMap(group, ",");

            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static Map<String, String[]> toMap(String value) {
        return toMap(value, ";");
    }

    public static String join(CharSequence delimiter, Object[] objects) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(objects);

        StringJoiner joiner = new StringJoiner(delimiter);
        int length = objects.length;

        for (int i = 0; i < length; i++) {
            joiner.add(objects[i].toString());
        }

        return joiner.toString();
    }


    private String join(String delimiter, List<String> value) {
        return join(delimiter, value);
    }

    public static String join(CharSequence delimiter, List<String> objects) {
        Objects.requireNonNull(objects);

        return join(delimiter, objects.toArray());
    }

    public static String normalize(String src) {
        String dest = Normalizer.normalize(src, Normalizer.Form.NFD);
        dest = dest.replaceAll("[^\\p{ASCII}]", "");

        return dest;
    }
}
