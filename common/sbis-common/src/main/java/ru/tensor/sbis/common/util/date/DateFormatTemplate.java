package ru.tensor.sbis.common.util.date;

/**
 * @author am.boldinov
 */
public enum DateFormatTemplate {
    ONLY_TIME("HH:mm"),
    ONLY_TIME_AM_PM("h:mm a"),
    DAY_WITH_MONTH("d MMMM"),
    DAY_WITH_SHORT_MONTH("d MMM"),
    DATE_SPLIT_BY_POINTS("dd.MM.yyyy"),
    DATE_SPLIT_BY_POINTS_WITH_TIME("dd.MM.yyyy HH:mm"),
    DATE_SPLIT_BY_POINTS_WITH_TIME_SHORT_YEAR("dd.MM.yy HH:mm"),
    DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR("dd.MM.yy"),
    DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR_AND_SHORT_WEEK("dd.MM.yy EEE"),
    WITHOUT_YEAR("d MMM HH:mm"),
    WITH_YEAR("d MMM yyyy HH:mm"),
    WITHOUT_YEAR_SPLIT_BY_COMMA("dd MMM, HH:mm"),
    WITH_YEAR_SPLIT_BY_COMMA("dd MMM yyyy, HH:mm"),
    DATE_SPLIT_BY_HYPHEN("yyyy-MM-dd"),
    LONG_DATE("dd MMMM yyyy"),
    LONG_DATE_WITHOUT_LEADING_ZERO("d MMMM yyyy"),
    LONG_DATE_SHORT_MONTH("dd MMM yyyy"),
    SHORT_DATE_SHORT_MONTH_SHORT_YEAR("d MMM'' yy"),
    LONG_DATE_WITH_TIME("dd MMMM yyyy HH:mm"),
    DATE_WITHOUT_YEAR("dd.MM"),
    DATE_WITH_TIME_WITHOUR_YEAR("dd MMM HH:mm"),
    DATE_WITH_FULL_MONTH_AND_TIME_WITHOUT_YEAR("dd LLLL HH:mm"),
    DATE_WITH_FULL_MONTH_SHORT_DAY_WITH_YEAR("d MMMM''yy"),
    DATE_WITH_FULL_MONTH_AND_TIME_SHORT_DAY("d MMMM HH:mm"),
    DATE_WITH_FULL_MONTH_AND_TIME_SHORT_DAY_WITH_YEAR("d MMMM''yy HH:mm"),
    DATE_WITH_SHORT_MONTH_AND_TIME_SHORT_DAY_WITH_YEAR("d MMM''yy HH:mm"),
    DATE_WITH_FULL_MONTH_AND_TIME_WITHOUT_YEAR_GENITIVE("dd MMMM HH:mm"),
    DATE_WITH_FULL_MONTH_AND_TIME_WITH_SECONDS_WITHOUT_YEAR_GENITIVE("dd MMMM HH:mm:ss"),
    DATE_WITH_FULL_MONTH_AND_TIME_WITH_YEAR_GENITIVE("dd MMMM''yy HH:mm"),
    DATE_WITH_SHORT_DAY_SHORT_MONTH_SHORT_YEAR_WITH_TIME("d.MM.yy HH:mm"),
    SHORT_DATE_WITH_YEAR("d MMM yy"),
    STANDARD_DAY_MONTH_TIME("d MMM HH:mm"),
    STANDARD_DAY_MONTH_YEAR("d MMM yyyy"),
    STANDARD_DAY_MONTH_YEAR_TIME("d MMM yyyy HH:mm"),
    STANDARD_DAY_NUMBER_MONTH_YEAR_TIME("dd.MM.yyyy HH:mm"),
    STANDARD_DAY_NUMBER_MONTH_TIME("dd.MM HH:mm"),
    DAY_WITH_SHORT_MONTH_AND_WEEK("d MMM (EEE)"),
    DATE_WITHOUT_YEAR_WITH_WEEK_SPLIT_BY_COMMA("dd MMMM, EEE HH:mm"),
    SHORT_WEEK_WITH_DAY_AND_SHORT_MONTH("EEE d MMM"),
    SHORT_WEEK("EEE"),
    LONG_WEEK("EEEE"),
    SHORT_MONTH("MMM"),
    LONG_DATE_SHORT_MONTH__AND_WEEK("dd MMM yyyy (EEE)"),
    ONLY_DIGITS("ddMMyyyyhhmmss"),
    MONTH_WITH_YEAR("LLLL yyyy"),//LLLL Полное имя месяца в именительном падеже
    MONTH_WITH_SHORT_YEAR("LLLL''yy"),
    ONLY_MONTH("LLLL"),
    ONLY_MONTH_GENITIVE("MMMM"),
    ONLY_YEAR("yyyy"),
    ONLY_DAY("d");

    DateFormatTemplate(String template) {
        this.template = template;
    }

    private final String template;

    public String getTemplate() {
        return template;
    }
}
