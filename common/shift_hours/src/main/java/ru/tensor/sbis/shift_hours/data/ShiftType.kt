package ru.tensor.sbis.shift_hours.data

/**
 * Тип смены в графике
 *
 */
enum class ShiftType  {
    /**
     * Утреняя (началась до полуночи текущего дня)
     */
    MORNING,

    /**
     * Обычная смена
     */
    NORMAL,

    /**
     * Вечерняя (началась после полуночи текущего дня)
     */
    NIGHT
}