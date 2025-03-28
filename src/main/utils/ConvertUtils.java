package uk.gov.dwp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static uk.gov.dwp.data.DateTimeConstants.*;

public class ConvertUtils {

    public static String booleanToString(boolean value) {
        return value ? "Yes" : "No";
    }

    public static boolean stringToBoolean(String text) {
        switch (text.toUpperCase()) {
            case "YES":
                return true;
            case "NO":
                return false;
            default:
                throw new UnsupportedOperationException("Text value should be 'Yes' or 'No' but the text is: " + text);
        }
    }

    public static LocalDateTime stringToLocalDateTime(String dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(CALENDER_DATE_TIME_FORMAT);
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    public static LocalDateTime stringToLocalDateTime(String dateTime, String format) {
        dateTime = dateTime.replace("AM", "am")
                .replace("PM", "pm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format,Locale.UK);
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    public static String localDateToXMLDate(LocalDate dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(XML_DATE_FORMAT);
        return dateTimeFormatter.format(dateTime);
    }

    public static String localDateToString(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dateTimeFormatter.format(localDate);
    }

    public static String localDateToString(LocalDate localDate,String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localDate);
    }

    public static String localDateToString2(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ALTERNATIVE_DATE_FORMAT);
        return dateTimeFormatter.format(localDate);
    }

    public static String localDateToString2(LocalDate localDate,String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localDate);
    }

    public static LocalDate stringToLocalDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT,Locale.UK);
        return LocalDate.parse(date, dateTimeFormatter);
    }

    public static LocalDate stringToLocalDate2(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ALTERNATIVE_DATE_FORMAT, Locale.UK);
        return LocalDate.parse(date, dateTimeFormatter);
    }

    public static LocalDate stringToLocalDate(String date, String dateFormatter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormatter);
        return LocalDate.parse(date, dateTimeFormatter);
    }
}
