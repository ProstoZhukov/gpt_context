package ru.tensor.sbis.common.util.date;

/**
 * @author am.boldinov
 */
abstract class DateFixer {
    public final String pattern;

    public DateFixer(String targetPattern) {
        pattern = targetPattern;
    }

    public abstract String fixDateString(String dateString);
}
