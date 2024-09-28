package ru.tensor.sbis.common.util.date;

import android.content.Context;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.util.dateperiod.FormatPart;
import static ru.tensor.sbis.common.util.dateperiod.FormatUtilsKt.getChangedFormat;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public class DateFormatUtils extends BaseDateUtils {
    private static final long MINUTES_PER_DAY = DateUtils.MILLIS_PER_DAY / DateUtils.MILLIS_PER_MINUTE;
    private static final long MINUTES_PER_HOUR = 60;

    /**
     * Дефолтный форматтер с дефолтной локалью
     * Исключительно для использования внутри класса утилит
     * (если отдать ссылку внешнему объекту, может быть конфликт
     * при работе с парсером одновременно из разных потоков)
     */
    private static final SimpleDateFormat innerDefaultFormatter = new SimpleDateFormat("HH:mm", DEFAULT_LOCALE);

    /**
     * Прокидывает параметры до метода {@link #getFormatter(String, Locale, TimeZone)}
     */
    @NonNull
    public static DateFormat getFormatter(@NonNull String template) {
        return getFormatter(template, Locale.getDefault(), getDeviceTimeZone());
    }

    /**
     * Прокидывает параметры до метода {@link #getFormatter(String, Locale, TimeZone)}
     */
    @NonNull
    public static DateFormat getFormatter(@NonNull String template, @NonNull Locale locale) {
        return getFormatter(template, locale, getDeviceTimeZone());
    }

    /**
     * Прокидывает параметры до метода {@link #getFormatter(String, Locale, TimeZone)}
     */
    @NonNull
    public static DateFormat getFormatter(@NonNull String template, TimeZone timeZone) {
        return getFormatter(template, Locale.getDefault(), timeZone);
    }

    /**
     * Получить новый настроенный instance форматтера
     *
     * @param template - шаблон
     * @param locale   - локаль
     * @param timeZone - часовой пояс
     * @return - настроенный форматтер
     */
    @NonNull
    public static DateFormat getFormatter(@NonNull String template, @NonNull Locale locale, TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(template, locale);
        format.setTimeZone(timeZone);
        return format;
    }

    /**
     * Получить внутренний форматтер с заданным шаблоном, локалью и часовым поясом
     *
     * @param template - шаблон
     * @param locale   - локаль
     * @param timeZone - часовой пояс
     * @return
     */
    private static DateFormat getInnerFormatter(@NonNull String template, @NonNull Locale locale, TimeZone timeZone) {
        SimpleDateFormat format;

        if (locale.equals(DEFAULT_LOCALE)) {
            format = innerDefaultFormatter;
            format.applyPattern(template);
        } else {
            format = new SimpleDateFormat(template, locale);
        }
        format.setTimeZone(timeZone);
        return format;
    }


    /**
     * Получить строковое представление даты с заданным шаблоном, локалью и часовым поясом
     *
     * @param date     - дата
     * @param template - шаблон
     * @param locale   - локаль
     * @param timeZone - временная зона
     * @return - строковое представление даты
     */
    public static synchronized String format(Date date, @NonNull String template, @NonNull Locale locale, TimeZone timeZone) {
        return getInnerFormatter(template, locale, timeZone).format(date);
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull DateFormatTemplate template, @NonNull Locale locale, TimeZone timeZone) {
        return format(date, template.getTemplate(), locale, timeZone);
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull String template, @NonNull Locale locale) {
        return format(date, template, locale, getDeviceTimeZone());
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull DateFormatTemplate template, @NonNull Locale locale) {
        return format(date, template.getTemplate(), locale);
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull String template, TimeZone timeZone) {
        return format(date, template, Locale.getDefault(), timeZone);
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull DateFormatTemplate template, TimeZone timeZone) {
        return format(date, template.getTemplate(), timeZone);
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull String template) {
        return format(date, template, Locale.getDefault(), getDeviceTimeZone());
    }

    /**
     * Прокидывает параметры до метода {@link #format(Date, String, Locale, TimeZone)}
     */
    public static String format(Date date, @NonNull DateFormatTemplate template) {
        return format(date, template.getTemplate());
    }

    /**
     * Прокидывает параметры до метода {@link #formatForRegistry(Date)}
     */
    public static String formatForRegistry(long timestamp) {
        return formatForRegistry(new Date(timestamp));
    }

    /**
     * Форматирует дату для реестров
     *
     * @param date - дата
     * @return строковое представление специфицированное для реестров
     */
    public static String formatForRegistry(@NonNull Date date) {
        if (isTheSameDay(new Date(), date)) {
            return format(date, DateFormatTemplate.ONLY_TIME);
        }

        if (isThisYear(date)) {
            return format(date, DateFormatTemplate.DATE_WITH_TIME_WITHOUR_YEAR);
        }

        return format(date, DateFormatTemplate.LONG_DATE_SHORT_MONTH);
    }

    public static String formatAsBirthday(long timestamp) {
        final Date date = new Date(timestamp);
        return format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS);
    }

    /**
     * Форматирует дату для отображения c префиксом дня (сегодня/вчера)
     *
     * @param context
     * @param date                   дата
     * @param withTimeForAnotherYear нужно ли отображать точное время для даты другого года
     * @return строковое представление даты с префиксом дня
     */
    public static String formatDateOrTimeWithPrefix(@NonNull Context context,
                                                    @NonNull Date date,
                                                    boolean withTimeForAnotherYear) {
        try {
            if (isTheSameDay(new Date(), date)) {
                return getTimeWithPrefix(date, context.getString(R.string.common_date_format_today));
            } else if (isYesterday(date)) {
                return getTimeWithPrefix(date, context.getString(R.string.common_date_format_yesterday));
            } else if (isThisYear(date)) {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_TIME);
            } else if (withTimeForAnotherYear) {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_YEAR_TIME);
            } else {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_YEAR);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирует дату для отображения c префиксом дня(сегодня/завтра/вчера)
     * <ul>
     * <li>Если сегодня - возвращает "сегодня ЧЧ:ММ"</li>
     * <li>Если завтра - возвращает "завтра ЧЧ:ММ"</li>
     * <li>Если вчера - возвращает "вчера ЧЧ:ММ"</li>
     * <li>Если в этом году - возвращает "Д мес. ЧЧ:ММ"</li>
     * <li>Если не в этом году - возвращает "ДД.ММ.ГГГГ ЧЧ:ММ"</li>
     * </ul>
     *
     * @param context context
     * @param date    дата
     * @return строковое представление даты и времени с префиксом дня
     */
    public static String formatDateAndTimeWithPrefix(@NonNull Context context,
                                                     @NonNull Date date) {
        try {
            if (isTheSameDay(new Date(), date)) {
                return getTimeWithPrefix(date, context.getString(R.string.common_date_format_today));
            } else if (isTomorrow(date)) {
                return getTimeWithPrefix(date, context.getString(R.string.common_date_format_tomorrow));
            } else if (isYesterday(date)) {
                return getTimeWithPrefix(date, context.getString(R.string.common_date_format_yesterday));
            } else if (isThisYear(date)) {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_TIME);
            } else {
                return format(date, DateFormatTemplate.STANDARD_DAY_NUMBER_MONTH_YEAR_TIME);
            }
        } catch (Exception e) {
            Timber.e(e);
            return "";
        }
    }

    /**
     * Форматирует дату для отображения по стандарту
     *
     * @param timestamp              дата
     * @param withTimeForAnotherYear нужно ли отображать точное время для даты другого года
     * @return строковое представление даты по стандарту http://axure.tensor.ru/MobileAPP/дата_и_время.html
     */
    public static String formatStandardDateOrTime(long timestamp,
                                                  boolean withTimeForAnotherYear) {
        return formatStandardDateOrTime(new Date(timestamp), withTimeForAnotherYear);
    }

    /**
     * Форматирует дату для отображения по стандарту
     *
     * @param date                   дата
     * @param withTimeForAnotherYear нужно ли отображать точное время для даты другого года
     * @return строковое представление даты по стандарту http://axure.tensor.ru/MobileAPP/дата_и_время.html
     */
    public static String formatStandardDateOrTime(@NonNull Date date,
                                                  boolean withTimeForAnotherYear) {
        try {
            if (isTheSameDay(new Date(), date)) {
                return formatDateOnlyTime(date);
            } else if (isThisYear(date)) {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_TIME);
            } else if (withTimeForAnotherYear) {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_YEAR_TIME);
            } else {
                return format(date, DateFormatTemplate.STANDARD_DAY_MONTH_YEAR);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирует дату:
     * <ul>
     * <li>Если сегодня - возвращает только время (14:56)</li>
     * <li>Если в этом году - возвращает число и месяц (23.08)</li>
     * <li>Если не в этом году - возвращает число, месяц и год (23.08.13)</li>
     * </ul>
     *
     * @param date дата
     * @return строковое представление даты по стандарту из
     * <b><a href=https://online.sbis.ru/opendoc.html?guid=6a91ca08-d256-4f0c-9e68-ed5b29c3d1bc>задачи</a></b>
     */
    public static String formatDateOrTimeUsingOnlyNumbers(Date date) {
        return formatDateOrTimeWithTodayOnlyTime(date,
                DateFormatTemplate.DATE_WITHOUT_YEAR,
                DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR);
    }

    /**
     * Форматирует дату:
     * <ul>
     * <li>Если сегодня - возвращает только время (14:56)</li>
     * <li>Если в этом году - возвращает число и краткое наименование месяца (23 авг.)</li>
     * <li>Если не в этом году - возвращает число, краткое наименование месяца и год (23 авг. 2018)</li>
     * </ul>
     *
     * @param timestamp дата
     * @return строковое представление даты по стандарту из
     * <b><a href=https://online.sbis.ru/opendoc.html?guid=d5f333d4-f5c3-4ec9-980b-d2ef8264d2a6>задачи</a></b>
     */
    public static String formatDateOrTimeWithTodayOnlyTime(long timestamp) {
        return formatDateOrTimeWithTodayOnlyTime(new Date(timestamp),
                DateFormatTemplate.DAY_WITH_SHORT_MONTH,
                DateFormatTemplate.STANDARD_DAY_MONTH_YEAR);
    }

    /**
     * Форматирует дату:
     * <ul>
     * <li>Если сегодня - возвращает только время (14:56)</li>
     * <li>Если в этом году - возвращает число и месяц согласно переданному шаблону</li>
     * <li>Если не в этом году - возвращает число, месяц и год согласно переданному шаблону</li>
     * </ul>
     *
     * @param date дата
     * @return строковое представление даты по переданным шаблонам
     */
    public static String formatDateOrTimeWithTodayOnlyTime(@NonNull Date date,
                                                           @NonNull DateFormatTemplate thisYearFormat,
                                                           @NonNull DateFormatTemplate otherYearFormat) {
        try {
            if (isTheSameDay(new Date(), date)) {
                return formatDateOnlyTime(date);
            } else {
                return formatDateDependingOnYear(date, thisYearFormat, otherYearFormat);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирует дату:
     * <ul>
     * <li>Если в этом году - возвращает число и месяц согласно первому переданному шаблону</li>
     * <li>Если не в этом году - возвращает число, месяц и год согласно второму переданному шаблону</li>
     * </ul>
     *
     * @param date дата
     * @return строковое представление даты по переданным шаблонам
     */
    @Nullable
    public static String formatDateDependingOnYear(@NonNull Date date,
                                                   @NonNull DateFormatTemplate thisYearFormat,
                                                   @NonNull DateFormatTemplate otherYearFormat) {
        try {
            if (isThisYear(date)) {
                return format(date, thisYearFormat);
            } else {
                return format(date, otherYearFormat);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирует дату и заменяет ее часть.
     * <ul>
     * <li>Если в этом году - возвращает дату согласно первому переданному шаблону.</li>
     * <li>Если не в этом году - возвращает дату согласно второму переданному шаблону.</li>
     * <li>Заменяет часть формата при наличии в нём соответствующего компонента.</li>
     * </ul>
     *
     * @param date              Дата.
     * @param thisYearFormat    Формат для текущего года.
     * @param otherYearFormat   Формат для не текущего года.
     * @param part              Маска и текст для замены.
     * @return                  Строковое представление даты по переданным шаблонам.
     */
    @Nullable
    public static String formatDateWithCustomPartDependingOnYear(
        @NonNull Date date,
        @NonNull DateFormatTemplate thisYearFormat,
        @NonNull DateFormatTemplate otherYearFormat,
        @NonNull FormatPart part
    ) {
        try {
            if (isThisYear(date)) {
                return format(date, getChangedFormat(thisYearFormat, part));
            } else {
                return format(date, getChangedFormat(otherYearFormat, part));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирует дату для отображения времени с префиксом
     *
     * @param date  дата
     * @param value префикс
     * @return строковое представление даты в виде "строка-префикс" + время
     */
    public static String getTimeWithPrefix(@NonNull Date date, @NonNull String value) {
        String time = formatDateOnlyTime(date);
        if (time != null) {
            return value.concat(" ").concat(time);
        }
        return value;
    }

    @Nullable
    public static String formatDateFromNow(@NonNull Date date) {
        return formatDateFromNow(date, false);
    }

    // Выводит время относительно текущего времени в виде отформатированной строки.
    public static String formatDateFromNow(@NonNull Date date, boolean isLongDate) {
        try {
            if (isTheSameDay(new Date(), date)) {
                return format(date, DateFormatTemplate.ONLY_TIME);
            } else {
                if (isLongDate) {
                    return format(date, DateFormatTemplate.DAY_WITH_MONTH);
                } else {
                    return format(date, DateFormatTemplate.DAY_WITH_SHORT_MONTH);
                }
            }
        } catch (Exception e) {
            Timber.d(e);
        }
        return null;
    }

    public static String formatDateOnlyTime(Date date) {
        try {
            return format(date, DateFormatTemplate.ONLY_TIME);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Форматирование даты для отображения оставшегося времени
     * <p>
     * [getTimeAsTermFormat]
     */
    public static String getTimeAsTermFormat(@NonNull Context context, Long timeStamp, boolean shortDayFormat) {
        return getTimeAsTermFormat(context, timeStamp, shortDayFormat, false);
    }

    /**
     * Форматирование даты для отображения оставшегося времени
     * <p>
     * если времени меньше 1 минуты вернет null
     * если времени меньше 1 часа вернет минуты
     * если времени меньше дня, вернет часы и минуты
     * если времени больше дня, в режиме shortDayFormat = true вернет только дни
     * если времени больше дня, в режиме absentDayFormat = true вернет только часы и минуты
     * иначе вернет дни, часы, минуты
     *
     * @param timeStamp       время
     * @param shortDayFormat  режим отображения времени больше суток
     * @param absentDayFormat режим отображения времени только в часах и минутах
     * @return строка для отображения
     */

    public static String getTimeAsTermFormat(@NonNull Context context, Long timeStamp, boolean shortDayFormat, boolean absentDayFormat) {
        if (timeStamp == null) {
            return null;
        }

        long timeInMinute = timeStamp / DateUtils.MILLIS_PER_MINUTE;

        if (timeInMinute < 1) {
            return null;
        }

        if (timeInMinute < MINUTES_PER_HOUR) {
            return context.getString(R.string.common_term_only_minutes_date_format,
                    timeInMinute);
        } else if (timeInMinute < MINUTES_PER_DAY) {
            return context.getString(
                    R.string.common_term_hours_and_minutes_date_format,
                    timeInMinute % MINUTES_PER_DAY / MINUTES_PER_HOUR,
                    timeInMinute % MINUTES_PER_HOUR);
        } else if (shortDayFormat) {
            return context.getString(R.string.common_term_only_days_date_format,
                    timeInMinute / MINUTES_PER_DAY);
        } else if (absentDayFormat) {
            return context.getString(R.string.common_term_hours_and_minutes_date_format,
                    timeInMinute / MINUTES_PER_HOUR,
                    timeInMinute % MINUTES_PER_HOUR);
        }

        return context.getString(R.string.common_term_days_hours_minutes_date_format,
                timeInMinute / MINUTES_PER_DAY,
                timeInMinute % MINUTES_PER_DAY / MINUTES_PER_HOUR,
                timeInMinute % MINUTES_PER_HOUR);
    }

    /**
     * Формирование строки для отображения оставшегося времени
     *
     * @param timeStamp дата, относительно которой вычисляется оставшеется время
     * @return отформатированная строка для отображения
     */
    public static String formatDateAsTerm(@NonNull Context context, @Nullable Long timeStamp) {
        if (timeStamp == null || timeStamp < 0) {
            return null;
        }
        long currentTime = System.currentTimeMillis();
        if (timeStamp < currentTime) {
            return null;
        }
        return getTimeAsTermFormat(context, timeStamp - currentTime, true);
    }
}
