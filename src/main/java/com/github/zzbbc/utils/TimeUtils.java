package com.github.zzbbc.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static String DEFAULT_DATE_TIME_FOMATTER = "dd/MM/YYYY HH:mm:ss";
    private static String DATE_TIME_FOMATTER = "YYYYMMddHHmmss";
    public static final String DATE_FORMATTER = "YYYYMMdd";

    public static String now() {
        return now(DEFAULT_DATE_TIME_FOMATTER);
    }

    public static String now(String formatter) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatter));
    }

    public static String time() {
        return now(DATE_TIME_FOMATTER);
    }

    public static String parse(LocalDateTime dateTime) {
        if (dateTime != null) {
            return parse(dateTime, DEFAULT_DATE_TIME_FOMATTER);
        }

        return "";
    }

    public static String parse(LocalDateTime dateTime, String formatter) {
        if (dateTime != null) {
            return dateTime.format(DateTimeFormatter.ofPattern(formatter));
        }

        return "";
    }

    public static boolean compareTime(LocalDateTime source, LocalDateTime destination) {
        return source.compareTo(destination) < 0;
    }

    public static long toEpochMilli() {
        Instant instant = Instant.now();

        long epoch = instant.toEpochMilli();

        return epoch;
    }
}
