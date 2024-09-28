package ru.tensor.sbis.common.util.date;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author am.boldinov
 */
public class DateParseUtils extends BaseDateUtils {

    /**
     * Дефолтный парсер с дефолтной локалью
     * Исключительно для использования внутри класса утилит
     * (если отдать ссылку внешнему объекту, может быть конфликт
     * при работе с парсером одновременно из разных потоков)
     */
    private static final SimpleDateFormat innerDefaultFormatter = new SimpleDateFormat("HH:mm", DEFAULT_LOCALE);

    /**
     * Прокидывает параметры до метода {@link #getParser(String, Locale)}
     * @param template - шаблон
     * @return - настроенный парсер
     */
    @NonNull
    public static DateFormat getParser(@NonNull String template) {
        return getParser(template, Locale.getDefault());
    }

    /**
     * Получить парсер с заданными шаблоном и локалью
     * @param template - шаблон
     * @param locale - локаль
     * @return - настроенный парсер
     */
    @NonNull
    public static DateFormat getParser(@NonNull String template, @NonNull Locale locale) {
        return new SimpleDateFormat(template, locale);
    }

    /**
     * Получить внутренний парсер с заданными шаблоном и локалью
     * @param template - шаблон
     * @param locale - локаль
     * @return - настроенный внутренний парсер, либо новый настроенный объект
     */
    private static DateFormat getInnerParser(@NonNull String template, @NonNull Locale locale) {
        SimpleDateFormat parser;

        if (locale.equals(DEFAULT_LOCALE)) {
            parser = innerDefaultFormatter;
            parser.applyPattern(template);
        } else {
            parser = new SimpleDateFormat(template, locale);
        }
        parser.setTimeZone(getDeviceTimeZone());
        return parser;
    }


    /**
     * Прокидывает параметры до метода {@link #parseDate(String, String, Locale)}
     * @return - дата из строки либо null
     */
    @Nullable
    public static Date parseDate(@NonNull String dateString, @NonNull String template) {
        return parseDate(dateString, template, Locale.getDefault());
    }

    /**
     * Прокидывает параметры до метода {@link #parseDate(String, String, Locale)}
     * @return - дата из строки либо null
     */
    @Nullable
    public static Date parseDate(String dateString, @NonNull DateParseTemplate template) {
        return parseDate(
                template.fixString(dateString),
                template.getCorrectPattern());
    }

    /**
     * Парсит строку с датой по шаблону с заданной локалью
     * @param dateString - строка с датой
     * @param template - шаблон
     * @param locale - локаль
     * @return - дата из строки
     */
    public static synchronized Date parseDate(@NonNull String dateString, @NonNull String template, @NonNull Locale locale) {
        DateFormat parser = getInnerParser(template, locale);
        try {
            return parser.parse(dateString);
        } catch (ParseException e1) {
            try {
                return parser.parse(adaptTimezone(dateString));
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    /**
     * Прокидывает параметры до метода {@link #parseDate(String, String, Locale)}
     * @return - дата из строки либо null
     */
    @Nullable
    public static Date parseDate(String dateString, @NonNull DateParseTemplate template, @NonNull Locale locale) {
        return parseDate(
                template.fixString(dateString),
                template.getCorrectPattern(),
                locale);
    }

    /**
     * Универсальный парсер(перебирает несколько основных шаблонов)
     * @param dateString - строка с датой
     * @return - дата из строки
     */
    @Nullable
    public static Date parseDate(@Nullable String dateString) {
        if (dateString == null) {
            return null;
        }

        Date date = parseDateWithTime(adaptTimezone(dateString));

        if (date == null) {
            date = parseDate(dateString, DateParseTemplate.ONLY_DATE);
        }

        return date;
    }

    /**
     * Парсит строку с датой последовательностью шаблонов
     * @param dateString - строка с датой
     * @param templateSequence - последовательность шаблонов
     * @return - первый успешный результат парсинга, либо null, если успешного результата не было
     */
    public static Date parseDate(String dateString, @NonNull DateParseTemplate... templateSequence) {
        Date date;
        for (DateParseTemplate dateParseTemplate : templateSequence) {
            if ((date = parseDate(dateString, dateParseTemplate)) != null) {
                return date;
            }
        }
        return null;
    }

    /**
     * Парсит строку с датой последовательностью шаблонов
     * @param dateString - строка с датой
     * @param templateSequence - последовательность шаблонов
     * @return - первый успешный результат парсинга либо null, если успешного результата не было
     */
    public static Date parseDate(@NonNull String dateString, @NonNull String... templateSequence) {
        Date date;
        for (String template : templateSequence) {
            if ((date = parseDate(dateString, template)) != null) {
                return date;
            }
        }
        return null;
    }

    /**
     * Адаптирует входную строку для парсинга таймзоны
     * @param dateString - входная строка
     * @return - адаптированная строка
     */
    @NonNull
    public static String adaptTimezone(@NonNull String dateString) {
        return Build.VERSION.SDK_INT >= 24
                ? dateString.concat("00")
                : dateString;
    }

    /**
     * Перебирает несколько основных шаблонов парсинга даты
     * @param dateString - строка с датой
     * @return - дата в строке либо null, если ни один шаблон не подошел
     */
    @Nullable
    private static Date parseDateWithTime(String dateString) {
        return parseDate(dateString,
                DateParseTemplate.WITH_SHORT_MILLISECONDS,
                DateParseTemplate.WITHOUT_MILLISECONDS,
                DateParseTemplate.WITH_LONG_MILLISECONDS);
    }
}
