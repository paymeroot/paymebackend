package com.payme.backend.web.utils;

import com.payme.backend.config.Constants;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {

    // Private constructor to prevent instantiation
    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Gets the current instant.
     *
     * @return the current instant
     */
    public static Instant getCurrentInstant() {
        return Instant.now();
    }

    public static String formatInstant(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_FORMAT);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

    public static Instant getInstantFromString(String date) {
        LocalDate localDate = getLocalDateFromString(date, Constants.DATE_TIME_PATTERN_FORMAT);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDate getLocalDateFromString(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }
}
