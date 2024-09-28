package ru.tensor.sbis.common.util.date;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public enum DateParseTemplate {
    // Шаблоны с временем, учет часов с 1 до 24
    WITHOUT_MILLISECONDS("yyyy-MM-dd kk:mm:ssZZZ"),
    WITH_SHORT_MILLISECONDS("yyyy-MM-dd kk:mm:ss.SSSZZZ"),
    WITH_LONG_MILLISECONDS("yyyy-MM-dd kk:mm:ss.SSSSSSZZZ",
            new DateFixer("yyyy-MM-dd kk:mm:ss.SSSZZZ") {
                @NonNull
                @Override
                public String fixDateString(@NonNull String dateString) {
                    return dateString.substring(0, 23).concat(dateString.substring(26, 29));
                }
            }),
    NO_TIMEZONE("yyyy-MM-dd kk:mm:ss"),
    WITH_LONG_MILLISECONDS_NO_TIMEZONE("yyyy-MM-dd kk:mm:ss.SSSSSS"),

    // Шаблоны без времени
    ONLY_DATE("yyyy-MM-dd"),
    DDMMYYYY_SPLIT_BY_DOTS("dd.MM.yyyy"),
    DDMMYY_SPLIT_BY_DOTS("dd.MM.yy"),
    DDMM_SPLIT_BY_DOT("dd.MM"),

    // Дубли шаблонов с временем, учет часов с 0 до 23
    WITHOUT_MILLISECONDS_0_23("yyyy-MM-dd HH:mm:ssZZZ"),
    WITH_SHORT_MILLISECONDS_0_23("yyyy-MM-dd HH:mm:ss.SSSZZZ"),
    WITH_LONG_MILLISECONDS_0_23("yyyy-MM-dd HH:mm:ss.SSSSSSZZZ",
            new DateFixer("yyyy-MM-dd HH:mm:ss.SSSZZZ") {
                @NonNull
                @Override
                public String fixDateString(@NonNull String dateString) {
                    return dateString.substring(0, 23).concat(dateString.substring(26, 29));
                }
            }),
    NO_TIMEZONE_0_23("yyyy-MM-dd HH:mm:ss"),
    WITH_LONG_MILLISECONDS_NO_TIMEZONE_0_23("yyyy-MM-dd HH:mm:ss.SSSSSS");

    DateParseTemplate(String pattern) {
        this.pattern = pattern;
        fixer = null;
    }

    DateParseTemplate(String originPattern, DateFixer fixer) {
        this.pattern = originPattern;
        this.fixer = fixer;
    }

    private final String pattern;
    @Nullable
    private final DateFixer fixer;

    public String getPattern() {
        return pattern;
    }

    public String getCorrectPattern() {
        return fixer != null
                ? fixer.pattern
                : getPattern();
    }

    public String fixString(String dateString) {
        if (fixer != null) {
            try {
                return fixer.fixDateString(dateString);
            } catch (Exception e) {
                String message = String.format(Locale.getDefault(),
                        "Fixing exception on string \"%s\"(pattern: %s)", dateString, getPattern());
                Timber.w(new RuntimeException(message), message);
            }
        }
        return dateString;
    }
}