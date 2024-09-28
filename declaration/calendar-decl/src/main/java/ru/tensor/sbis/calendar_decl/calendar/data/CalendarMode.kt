package ru.tensor.sbis.calendar_decl.calendar.data

/** Режим открытия календаря */
enum class CalendarMode {
    /** Календарь сотрудника */
    CALENDAR,

    /** Активность сотрудника */
    ACTIVITY,

    /** Статистика сотрудника за месяц */
    STATISTICS_MONTH,

    /** Статистика сотрудника за год */
    STATISTICS_YEAR,

    /** Статистика активности сотрудника за месяц */
    STATISTICS_MONTH_ACTIVITY;

    /** Является ли режим календаря экраном статистики */
    fun isStatistics() = this == STATISTICS_MONTH || this == STATISTICS_YEAR || this == STATISTICS_MONTH_ACTIVITY
}

/** Тип открываемого экрана календаря */
enum class OpeningViewType {
    /** Лента событий по дням */
    DAY,

    /** Лента событий по месяцам */
    MONTH,

    /** Лента событий по годам */
    YEAR
}

/** Тип источника открытия экрана календаря */
enum class SourceViewType {
    /**
     * Календарь
     * */
    CALENDAR,

    /**
     * График
     *
     */
    SCHEDULE,

    /**
     * Активность
     */
    ACTIVITY
}