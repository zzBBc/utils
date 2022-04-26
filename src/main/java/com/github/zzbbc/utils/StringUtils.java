package com.github.zzbbc.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletInputStream;
import com.google.gson.JsonArray;

public class StringUtils {
    private static final String DEFAULT_DELIMITER = ",";

    public static String toString(List<String> contacts) {
        return join(DEFAULT_DELIMITER, contacts);
    };

    public static String toString(String message, StackTraceElement[] stackTraces) {
        return toString(new StringBuilder(message), stackTraces);
    }

    public static String toString(ServletInputStream in) throws IOException {
        String result;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = in.read(buffer)) != -1;) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        result = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());

        return result;
    }

    public static String toString(StringBuilder prefix, Exception exception) {
        prefix = prefix.append(System.lineSeparator()).append(exception.getMessage());
        return toString(prefix, exception.getStackTrace());
    };

    public static String toString(StringBuilder prefix, StackTraceElement[] stackTraces) {
        return new StringBuilder(prefix).append(toStringBuilder(stackTraces)).toString();
    }

    private static Object toStringBuilder(StackTraceElement[] stackTraces) {
        StringBuilder builder = new StringBuilder(System.lineSeparator());

        for (StackTraceElement stackTraceElement : stackTraces) {
            builder.append(System.lineSeparator()).append(stackTraceElement);
        }

        return builder;
    }

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

    public String toString(Map<String, List<String>> list) {
        StringBuilder builder = new StringBuilder("[");

        list.forEach((key, value) -> {
            builder.append(key).append(": ").append(join(DEFAULT_DELIMITER, value));
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

    public static String join(JsonArray array) {
        StringJoiner joiner = new StringJoiner(DEFAULT_DELIMITER);
        int length = array.size();

        for (int i = 0; i < length; i++) {
            joiner.add(JsonUtils.getString(array, i));
        }

        return joiner.toString();
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


    private static String join(String delimiter, List<String> value) {
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

    public static boolean isEmpty(String src) {
        if (src != null) {
            return src.isEmpty();
        } else {
            return true;
        }
    }

    public static String convertToUnsign(String src) {
        String[] patterns = {"(á|à|ả|ã|ạ|ă|ắ|ằ|ẳ|ẵ|ặ|â|ấ|ầ|ẩ|ẫ|ậ)", "đ", "(é|è|ẻ|ẽ|ẹ|ê|ế|ề|ể|ễ|ệ)",
                "(í|ì|ỉ|ĩ|ị)", "(ó|ò|ỏ|õ|ọ|ô|ố|ồ|ổ|ỗ|ộ|ơ|ớ|ờ|ở|ỡ|ợ)", "(ú|ù|ủ|ũ|ụ|ư|ứ|ừ|ử|ữ|ự)",
                "(ý|ỳ|ỷ|ỹ|ỵ)"};
        char[] replaceChars =
                {'a', 'd', 'e', 'i', 'o', 'u', 'y', 'A', 'D', 'E', 'I', 'O', 'U', 'Y'};

        for (int i = 0; i < replaceChars.length; i++) {
            for (int j = 0; j < patterns.length; j++) {
                Pattern pattern = Pattern.compile(String.valueOf(patterns[j]));

                Matcher matcher = pattern.matcher(src);

                while (matcher.find()) {
                    int start = matcher.start();

                    char ch = Character.isLowerCase(src.charAt(start)) ? replaceChars[i + j]
                            : replaceChars[i + j + 7];

                    src = src.replace(src.charAt(start), ch);
                }
            }
        }

        return src;
    }

    public static String generateLength32() {
        String temp = UUID.randomUUID().toString();

        return temp.substring(0, 32);
    }
}
