package uk.gov.dwp.utils.dates;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import uk.gov.dwp.webdriver.utils.JsonUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static uk.gov.dwp.data.DateTimeConstants.ALTERNATIVE_DATE_FORMAT;
import static uk.gov.dwp.utils.dates.Locale.ENGLAND_AND_WALES;

public class DatesUtil {

    /**
     * Returns a string that represents the date that is the given days
     * ahead of the start date
     *
     * @param startDate  start date
     * @param daysToAdd  days to add to start date
     * @param dateFormat date format (e.g. "dd/MM/yyyy","dd/MM/yyyy HH:mm")
     * @return String representing resulting date
     */
    public static String futureDate(String startDate, int daysToAdd, String dateFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTime dateTime = DateTime.parse(startDate, dateTimeFormatter).plusDays(daysToAdd).toDateTime();
        return dateTime.toString(dateTimeFormatter);
    }

    /**
     * Returns a string that represents the date that is the given days
     * behind of the start date
     *
     * @param startDate    start date
     * @param daysToRemove days to remove from start date
     * @param dateFormat   date format (e.g. "dd/MM/yyyy","dd/MM/yyyy HH:mm")
     * @return String representing resulting date
     */
    public static String pastDate(String startDate, int daysToRemove, String dateFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTime dateTime = DateTime.parse(startDate, dateTimeFormatter).minusDays(daysToRemove).toDateTime();
        return dateTime.toString(dateTimeFormatter);
    }

    /**
     * Returns the current date for today
     *
     * @param dateFormat date format (e.g. "dd/MM/yyyy","dd/MM/yyyy HH:mm")
     * @return String representing resulting date
     */
    public static String now(String dateFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTime dateTime = DateTime.now().toDateTime();
        return dateTime.toString(dateTimeFormatter);
    }

    /**
     * @param startDate                 the date to start with
     * @param numberOfBusinessDaysToAdd Days to add, avoiding weekends
     * @param dateFormat                date format (e.g. "dd/MM/yyyy","dd/MM/yyyy HH:mm"")
     * @return String representing resulting date
     */
    public static String futureDateAvoidingWeekends(String startDate, int numberOfBusinessDaysToAdd, String dateFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTime futureDateTime = DateTime.parse(startDate, dateTimeFormatter).toDateTime();
        int addedDays = 0;
        while (addedDays < numberOfBusinessDaysToAdd) {
            futureDateTime = futureDateTime.plusDays(1);
            if (!((futureDateTime.getDayOfWeek() == 6) || (futureDateTime.getDayOfWeek() == 7))) {
                ++addedDays;
            }
        }
        return futureDateTime.toString(dateTimeFormatter);
    }

    public static String futureDateAvoidingWeekendsAndBankHolidays(String startDate,
                                                                   int numberOfBusinessDaysToAdd,
                                                                   String dateFormat) throws IOException {
        BankHolidays bankHolidays = getBankHolidays();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTime now = DateTime.parse(startDate, dateTimeFormatter).toDateTime();
        DateTime futureDateTime = DateTime.parse(futureDateAvoidingWeekends(startDate,
                getNumberOfBusinessDaysToAdd(numberOfBusinessDaysToAdd), dateFormat), dateTimeFormatter).toDateTime();
        LocalDate newDate = new LocalDate(futureDateTime);
        for (BankHoliday bankHoliday : bankHolidays.get(ENGLAND_AND_WALES)) {
            if (!bankHoliday.getLocalDate().isBefore(new LocalDate(now)) && !bankHoliday.getLocalDate().isAfter(newDate)) {
                newDate = newDate.plusDays(1);
                if (newDate.getDayOfWeek() == 6) {
                    newDate = newDate.plusDays(2);
                } else if (newDate.getDayOfWeek() == 7) {
                    newDate = newDate.plusDays(1);
                }
            }
        }
        return newDate.toString(dateTimeFormatter);
    }

    /**
     * Returning list of bank holidays
     *
     * @return getting the list of bank holidays
     * @throws IOException getting exception if unable to get or read bank holidays
     * Use below url to update the bank-holidays.json file
     * <a href="https://www.gov.uk/bank-holidays.json">...</a>
     */
    private static BankHolidays getBankHolidays() throws IOException {
        return JsonUtils.fromFile(
                ClassLoader.getSystemResourceAsStream("bank-holidays.json"), BankHolidays.class);
    }

    /**
     * Check the given date is bank holiday
     *
     * @param localDate date to check
     * @return returns true if the date is bank holiday otherwise false
     * @throws IOException getting exception if Bank holidays not retrieving
     */
    private static boolean isBankHoliday(java.time.LocalDate localDate) throws IOException {
        BankHolidays bankHolidays = DatesUtil.getBankHolidays();
        for (BankHoliday bankHoliday : bankHolidays.get(ENGLAND_AND_WALES)) {
            if (localDate.toString().equals(bankHoliday.getLocalDate().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returning boolean value
     *
     * @param date date to check
     * @return returns true if the given date of week is Saturday or sunday otherwise false
     */
    private static boolean isWeekend(java.time.LocalDate date) {
        return date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;
    }

    /**
     * Returning boolean value
     *
     * @param date date to check
     * @return returns true if the given date is not a business day
     * @throws IOException getting exception if Bank holidays not retrieving
     */
    public static boolean isNonBusinessDay(java.time.LocalDate date) throws IOException {
        return isBankHoliday(date) || isWeekend(date);
    }


    /**
     * Local date after subtracting the business days
     *
     * @param date                   date to subtract
     * @param businessDaysToSubtract number of business days to subtract
     * @return return Local date after subtracting the business days
     * @throws IOException getting exception if Bank holidays not retrieving
     */
    public static java.time.LocalDate getPastBusinessDayAvoidWeekends(java.time.LocalDate date,
                                                                      int businessDaysToSubtract) throws IOException {
        while (businessDaysToSubtract > 0) {
            date = date.minusDays(1);
            if (!isNonBusinessDay(date)) {
                businessDaysToSubtract--;
            }
        }
        return date;
    }

    public static LocalDateTime getDateTime(java.time.LocalDate date, int hours, int minutes) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hours, minutes);
    }

    public static String getCurrentDateWithXMinusHours(String dateTimeFormat, long minutesToMinus) {
        long currentDateTime = System.currentTimeMillis();
        DateTime currentDate = new DateTime(currentDateTime - minutesToMinus * 60 * 1000);
        return currentDate.toString(dateTimeFormat);
    }

    public static String getDateTimeAsString(java.time.LocalDate localDate, int hour, int minute, String pattern) {
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(getDateTime(localDate, hour, minute))
                .replace("am", "AM")
                .replace("pm", "PM");
    }

    public static String getDateTimeAsString(LocalDateTime localDateTime, String pattern) {
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime)
                .replace("am", "AM")
                .replace("pm", "PM");
    }

    public static String getTodayDate() {
        java.time.format.DateTimeFormatter dateTimeFormatter =
                java.time.format.DateTimeFormatter.ofPattern(ALTERNATIVE_DATE_FORMAT);
        return dateTimeFormatter.format(java.time.LocalDate.now());
    }

    public static String getPastCurrentOrFutureDate(int days) {
        DateFormat dateFormat = new SimpleDateFormat(ALTERNATIVE_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return dateFormat.format(calendar.getTime());
    }

    public static String getDateWithRequiredFormat(String strDate, String oldFormat, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date date = sdf.parse(strDate);
        sdf.applyPattern(newFormat);
        return sdf.format(date);
    }

    public static String getCurrentMonth() {
        return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    }

    private static int getNumberOfBusinessDaysToAdd(int daysToAdd) {
        DayOfWeek dayOfWeek = java.time.LocalDate.now().getDayOfWeek();
        if (dayOfWeek.equals(SATURDAY) || dayOfWeek.equals(SUNDAY))
            return daysToAdd + 1;
        else
            return daysToAdd;
    }

    public static String getTimeFromLocalDateTime(LocalDateTime localDateTime) {
        return getHour(localDateTime) + ":" + getMinute(localDateTime);
    }

    public static String getSurvellianceExpiryDate(String pattern) {
        return getDateTimeAsString(LocalDateTime.now().plusMonths(3).minusDays(1), pattern);
    }

    public static String getHour(LocalDateTime localDateTime) {
        return localDateTime.getHour() < 10 ? "0" + localDateTime.getHour() :
                String.valueOf(localDateTime.getHour());

    }

    public static String getMinute(LocalDateTime localDateTime) {
        return localDateTime.getMinute() < 10 ? "0" + localDateTime.getMinute() :
                String.valueOf(localDateTime.getMinute());

    }


}



