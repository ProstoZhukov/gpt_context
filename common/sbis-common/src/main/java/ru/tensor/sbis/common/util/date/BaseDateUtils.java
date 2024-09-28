package ru.tensor.sbis.common.util.date;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;

/**
 * @author am.boldinov
 */
public class BaseDateUtils {

    protected static final Locale DEFAULT_LOCALE = Locale.getDefault(); // Possible to set concrete default locale

    /**
     * Метод получения часового пояса устройства
     *
     * @return Часовой пояс устройста
     */
    @NonNull
    public static TimeZone getDeviceTimeZone() {
        return TimeZone.getDefault();
    }

    /**
     * Метод получения смещения часового пояса устройства в милисекундах
     *
     * @return Смещение часового пояса устройства в милисекундах
     */
    public static int getTimeZoneOffset() {
        return getTimeZoneOffset(getDeviceTimeZone());
    }

    /**
     * Метод получения смещения часового пояса в милисекундах
     *
     * @param timeZone Часовой пояс, смещение которого требуется получить
     * @return Смещение часового пояса в милисекундах
     */
    public static int getTimeZoneOffset(@NonNull TimeZone timeZone) {
        return timeZone.getRawOffset();
    }

    /**
     * Метод вычисления разницы в днях от первой даты до второй, учитывая смещение часового пояса устройства
     *
     * @param firstDate  Первая дата
     * @param secondDate Вторая дата
     * @return Разница в днях от первой даты до второй
     */
    public static int daysBetween(@NonNull Date firstDate, @NonNull Date secondDate) {
        return daysBetween(firstDate, secondDate, getTimeZoneOffset());
    }

    /**
     * Метод вычисления разницы в днях от первой даты до второй, учитывая смещение часового пояса
     *
     * @param firstDate      Первая дата
     * @param secondDate     Вторая дата
     * @param timeZoneOffset Смещение часового пояса
     * @return Разница в днях от первой даты до второй
     */
    public static int daysBetween(@NonNull Date firstDate, @NonNull Date secondDate, int timeZoneOffset) {
        long firstDay = (firstDate.getTime() + timeZoneOffset) / DateUtils.MILLIS_PER_DAY;
        long secondDay = (secondDate.getTime() + timeZoneOffset) / DateUtils.MILLIS_PER_DAY;
        return (int) (secondDay - firstDay);
    }

    /**
     * Метод вычисления разницы в днях от проверяемой даты до сегодняшнего дня, учитывая смещение часового пояса устройства
     *
     * @param checkDate Проверяемая дата
     * @return Разница в днях от проверяемой даты до сегодняшнего дня
     */
    public static int daysUntilToday(@NonNull Date checkDate) {
        return daysBetween(checkDate, currentDate());
    }

    /**
     * Метод определяет, соответствует ли проверяемая дата сегодняшнему дню
     *
     * @param checkDate Проверяемая дата
     * @return true, если проверяемая дата соответствует сегодняшнему дню, false - иначе
     */
    public static boolean isToday(@NonNull Date checkDate) {
        return daysUntilToday(checkDate) == 0;
    }

    /**
     * Метод определяет, соответствует ли проверяемая дата периоду после сегодняшнего дня
     *
     * @param checkDate Проверяемая дата
     * @return true, если проверяемая дата соответствует периоду после сегодняшнего дня, false - иначе
     */
    public static boolean isAfterToday(@NonNull Date checkDate) {
        return daysUntilToday(checkDate) < 0;
    }

    /**
     * Метод определяет, соответствует ли проверяемая дата периоду до сегодняшнего дня
     *
     * @param checkDate Проверяемая дата
     * @return true, если проверяемая дата соответствует периоду до сегодняшнего дня, false - иначе
     */
    public static boolean isBeforeToday(@NonNull Date checkDate) {
        return daysUntilToday(checkDate) > 0;
    }

    /**
     * Метод определяет, являются ли проверяемые даты сущностями одного дня
     *
     * @param firstDate  Первая дата
     * @param secondDate Вторая дата
     * @return true, если проверяемые даты являются сущностями одного дня, false - иначе
     */
    public static boolean isTheSameDay(@NonNull Date firstDate, @NonNull Date secondDate) {
        return daysBetween(firstDate, secondDate) == 0;
    }

    /**
     * Метод определяет, соответствует ли проверяемая дата вчерашнему дню
     *
     * @param checkDate Проверяемая дата
     * @return true, если проверяемая дата соответствует вчерашнему дню, false - иначе
     */
    public static boolean isYesterday(@NonNull Date checkDate) {
        return daysUntilToday(checkDate) == 1;
    }

    /**
     * Метод определяет, соответствует ли проверяемая дата завтрашнему дню
     *
     * @param checkDate Проверяемая дата
     * @return true, если проверяемая дата соответствует завтрашнему дню, false - иначе
     */
    public static boolean isTomorrow(@NonNull Date checkDate) {
        return daysUntilToday(checkDate) == -1;
    }

    /**
     * Метод определяет, соответствует ли месяц проверяемой даты текущему месяцу
     *
     * @param checkDate Проверяемая дата
     * @return true, если месяц проверяемой даты соответствует текущему месяцу, false - иначе
     */
    public static boolean isThisMonth(@NonNull Date checkDate) {
        return isSameMonth(currentDate(), checkDate);
    }

    /**
     * Метод определяет, являются ли проверяемые даты сущностями одного месяца
     *
     * @param firstDate  Первая дата
     * @param secondDate Вторая дата
     * @return true, если проверяемые даты являются сущностями одного месяца, false - иначе
     */
    public static boolean isSameMonth(@NonNull Date firstDate, @NonNull Date secondDate) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(secondDate);
        return (firstCalendar.get(Calendar.ERA) == secondCalendar.get(Calendar.ERA) &&
                firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
                firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH));
    }

    /**
     * Метод определяет, соответствует ли год проверяемой даты текущему году
     *
     * @param checkDate Проверяемая дата
     * @return true, если год проверяемой даты соответствует текущему году, false - иначе
     */
    public static boolean isThisYear(@NonNull Date checkDate) {
        Calendar now = Calendar.getInstance();
        Calendar check = Calendar.getInstance();
        check.setTime(checkDate);
        return now.get(Calendar.YEAR) == check.get(Calendar.YEAR);
    }

    /**
     * Метод возвращает текущее время в миллисекундах
     *
     * @return Текущее время в миллисекундах
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Метод возвращает текущую дату
     *
     * @return Текущая дата
     */
    @NonNull
    public static Date currentDate() {
        return new Date();
    }
}