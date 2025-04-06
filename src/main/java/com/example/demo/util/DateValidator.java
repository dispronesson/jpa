package com.example.demo.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateValidator {
    public static boolean isValidDate(String date) {
        if (date.length() != 10) {
            return false;
        }

        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
