package ru.tensor.sbis.design.list_header

/**
 * Режим отображения даты и времени во view. Позволяет определить для view,
 * какой именно аспект данных FormattedDateTime нужно отображать
 *
 * @author ra.petrov
 */
enum class DateViewMode {

    /**
     * По умолчанию - дата + время
     */
    DATE_TIME,

    /**
     * Отображение только даты
     */
    DATE_ONLY,

    /**
     * Отображение только времени
     */
    TIME_ONLY
}
