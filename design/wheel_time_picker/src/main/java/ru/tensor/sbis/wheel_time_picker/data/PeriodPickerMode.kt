package ru.tensor.sbis.wheel_time_picker.data

/** Режим пикера **/
enum class PeriodPickerMode {
    /** Начало */
    START,

    /** Длительность */
    DURATION,

    /** Дата и время */
    DATE_AND_TIME,

    /** Длительность в пределах одного дня */
    ONE_DAY_DURATION,

    /** Только дата */
    DATE_ONLY

}