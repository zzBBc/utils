package com.github.zzbbc.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static String DEFAULT_DATE_TIME_FOMATTER = "dd/MM/YYYY HH:mm:ss";
    private static String DATE_TIME_FOMATTER = "YYYYMMddHHmmss";

    public static String now() {
        return now(DEFAULT_DATE_TIME_FOMATTER);
    }

    public static String now(String formatter) {
        return timeNow().format(DateTimeFormatter.ofPattern(formatter));
    }

    private static LocalDateTime timeNow() {
        return LocalDateTime.now();
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
}
