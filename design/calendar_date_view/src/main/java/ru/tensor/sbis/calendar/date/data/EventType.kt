package ru.tensor.sbis.calendar.date.data

enum class EventType {
    WORKDAY,
    DAY_OFF,
    TRUANCY,
    BUSINESS_TRIP,
    PLAN_VACATION,
    PLAN_VACATION_ON_AGREEMENT,
    PLAN_VACATION_ON_DELETION,
    FACT_VACATION_MOBILIZATION,
    FACT_VACATION,
    FACT_VACATION_WITHOUT_PAY,
    SICK_LEAVE,
    DOWNTIME,
    BIRTHDAY,
    BABY_CARE,
    REPORT,
    NOT_HIRED,
    TIME_OFF
}

// флаги бордюров
internal const val TOP = 1 shl 0    // верхний бордюр
internal const val RIGHT = 1 shl 1  // правый бордир
internal const val BOTTOM = 1 shl 2 // нижний бордюр
internal const val LEFT = 1 shl 3   // левый бордюр
internal const val PIXEL_TL = 1 shl 4   // верхний левый пиксель
internal const val PIXEL_BR = 1 shl 5   // нижний правый пиксель

/**
 * Тип отрисовыемой ячейки
 * @param flags задает
 * @param isHoliday ячейка является праздничным днем
 */
sealed class SelectedType(var flags: Int = 0, var isHoliday: Boolean = false) {
    class NONE: SelectedType()
    class SINGLE: SelectedType()
    class FIRST: SelectedType()
    class MIDDLE: SelectedType()
    class LAST: SelectedType()
}

/**
 * Тип выбора даты
 * @param CLICK выбрана дата в результате клика по определенному дню
 * @param SCROLL выбрана дата в результате скролла LegendView
 */
enum class ModeSelectedDay {
    CLICK,
    SCROLL
}