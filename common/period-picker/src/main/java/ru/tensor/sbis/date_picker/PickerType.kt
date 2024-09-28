package ru.tensor.sbis.date_picker

/**
 * Назначение компонента выбора дат - выбор периода или выбор даты
 *
 * @author mb.kruglova
 */
enum class PickerType {
    DATE, // выбор даты
    PERIOD, // выбор периода
    PERIOD_BY_ONE_CLICK, // выбор периода по одному клику (без определения начала и конца периода)
    PERIOD_ONLY, // выбор периода без возможности выбора дня
    DATE_ONCE, // выбор даты "сразу же"
    MONTH_ONCE; // выбор месяца "сразу же"

    val isForDateSelection get() = this == DATE || this == DATE_ONCE
    val isForPeriodSelectionByOneClick get() = this == PERIOD_BY_ONE_CLICK
    val isForMonthOnceSelection get() = this == MONTH_ONCE
    val isForPeriodOnlySelection get() = this == PERIOD_ONLY
}