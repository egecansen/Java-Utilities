package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtilities {

    public static Calendar date;
    /**
     * Returns the current date and time as a Calendar object.
     *
     * @return The current date and time as a Calendar object.
     */
    public Calendar getDate(){return Calendar.getInstance();}

    /**
     * Returns a new Calendar object with the specified unit of time set to the specified value.
     * The unit of time can be one of "year", "month", "day", "hour", "minute", or "second".
     *
     * @param unitType The type of unit to set (year, month, day, hour, minute, or second).
     * @param value    The value to set the unit of time to.
     * @return A new Calendar object with the specified unit of time set to the specified value.
     */
    public Calendar getSetDate(String unitType, Integer value){return setDate(getDate(), unitType, value);}

    /**
     * Sets the specified unit of time in the provided Calendar object to the specified value.
     * The unit of time can be one of "year", "month", "day", "hour", "minute", or "second".
     *
     * @param calendar  The Calendar object to set the date on.
     * @param unitType  The type of unit to set (year, month, day, hour, minute, or second).
     * @param value     The value to set the unit of time to.
     * @return The modified Calendar object with the specified unit of time set to the specified value.
     */
    Calendar setDate(Calendar calendar, String unitType, Integer value){
        switch (unitType.toLowerCase()){
            case "year":
                calendar.set(Calendar.YEAR, value);
                break;

            case "day":
                calendar.set(Calendar.DAY_OF_MONTH, value);
                break;

            case "hour":
                calendar.set(Calendar.HOUR_OF_DAY, value);
                break;

            case "minute":
                calendar.set(Calendar.MINUTE, value);
                break;

            case "month":
                calendar.set(Calendar.MONTH, value);
                break;

            case "second":
                calendar.set(Calendar.SECOND, value);

            default:
                calendar.set(Calendar.MONTH,4);
                calendar.set(Calendar.DAY_OF_MONTH,1);
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                calendar.set(Calendar.MINUTE, 30);
                break;
        }
        return calendar;
    }

    /**
     * Parses a date string with the specified input format and reformats it into a new string
     * with the specified output format.
     *
     * @param dateString The date string to be reformatted.
     * @param inputFormat The format of the input date string.
     * @param outputFormat The format to which the date string should be reformatted.
     * @return A string representing the reformatted date.
     */
    public static String reformatDateString(String dateString, String inputFormat, String outputFormat) {
        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
        try {
            Date date = inputFormatter.parse(dateString);
            return outputFormatter.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null in case of a parsing error
        }
    }

    /**
     * Parses a date string with the specified input format and reformats it into a new string
     * with the specified output format.
     *
     * @param outputFormat The format to which the date string should be reformatted.
     * @return A string representing the reformatted date.
     */
    public static LocalDateTime reformatLocalDateTime(LocalDateTime inputDate, String outputFormat) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        return LocalDateTime.parse(inputDate.toString(), outputFormatter);
    }

    /**
     * Parses a date string with the specified input format and reformats it into a new string
     * with the specified output format.
     *
     * @param outputFormat The format to which the date string should be reformatted.
     * @return A string representing the reformatted date.
     */
    public static LocalDate reformatLocalDate(LocalDate inputDate, String outputFormat) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        return LocalDate.parse(inputDate.toString(), outputFormatter);
    }

    /**
     * Parses a date string using the specified date format and returns a LocalDate object.
     *
     * @param dateString  A string representing the date to parse.
     * @param dateFormat  A string specifying the format of the date string, following the DateTimeFormatter pattern.
     * @return A LocalDate object representing the parsed date.
     */
    public static LocalDate getLocalDateFor(String dateString, String dateFormat) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.parse(dateString, dateFormatter);
    }

    /**
     * Parses a date and time string using the specified date format and returns a LocalDateTime object.
     *
     * @param dateString  A string representing the date and time to parse.
     * @param dateFormat  A string specifying the format of the date and time string, following the DateTimeFormatter pattern.
     * @return A LocalDateTime object representing the parsed date and time.
     */
    public static LocalDateTime getLocalDateTimeFor(String dateString, String dateFormat) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.parse(dateString, dateFormatter);
    }

    /**
     * Parses a date and time string using the specified date format, converts it to an OffsetDateTime with the provided ZoneOffset,
     * and returns a formatted date string.
     *
     * @param dateString  A string representing the date and time to parse.
     * @param dateFormat  A string specifying the format of the date and time string, following the DateTimeFormatter pattern.
     * @param offset     The ZoneOffset to apply to the parsed date and time.
     * @return A formatted date string representing the OffsetDateTime with the specified ZoneOffset.
     */
    public static String getOffsetDateStringFor(String dateString, String dateFormat, ZoneOffset offset) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        OffsetDateTime currentOffsetDateTime = LocalDateTime.parse(dateString, dateFormatter).atOffset(offset);
        return currentOffsetDateTime.format(dateFormatter);
    }

    /**
     * Parses a date and time string using the specified date format, converts it to an OffsetDateTime with the provided ZoneOffset,
     * and returns a formatted date string.
     *
     * @param dateFormat  A string specifying the format of the date and time string, following the DateTimeFormatter pattern.
     * @param offset     The ZoneOffset to apply to the parsed date and time.
     * @return A formatted date string representing the OffsetDateTime with the specified ZoneOffset.
     */
    public static String getOffsetDateStringFor(LocalDateTime dateTime, String dateFormat, ZoneOffset offset) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTime.atOffset(offset).format(dateFormatter);
    }

    /**
     * Parses a date string with the specified input format and reformats it into a new string
     * with the specified output format.
     *
     * @param dateString The date string to be reformatted.
     * @return A string representing the reformatted date.
     */
    public static LocalDate getLocalDate(String dateString, String dateFormat) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.parse(dateString, inputFormatter);
    }

    /**
     * Calculates the number of days between two LocalDate objects.
     *
     * @param startDate The starting LocalDate.
     * @param endDate The ending LocalDate.
     * @return The number of days between the startDate and endDate.
     */
    public static long countDaysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
}
