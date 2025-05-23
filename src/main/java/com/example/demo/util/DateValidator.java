package com.example.demo.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateValidator {
    private DateValidator() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("checkstyle:CatchParameterName")
    public static boolean isValidDate(String date) {
        if (date.length() != 10) {
            return false;
        }

        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException _) {
            return false;
        }
    }
}
