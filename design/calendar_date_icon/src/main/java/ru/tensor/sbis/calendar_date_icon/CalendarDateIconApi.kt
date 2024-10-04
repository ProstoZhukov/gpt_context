package ru.tensor.sbis.calendar_date_icon

/**
 * API иконки календаря
 *
 * @author da.zolotarev
 */
interface CalendarDateIconApi {
    /**
     * Число на иконке
     * В случае null число не отображается.
     */
    var dayNumber: Int?

    /**
     * Размер компонента.
     */
    var size: Float

    /**
     * Высота числа на иконке.
     */
    fun getNumberHeight(): Int
}