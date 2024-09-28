package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model

import java.util.Calendar

/**
 * Ячейка календаря.
 *
 * @author mb.kruglova
 */
internal interface DayItemModel {

    /** Дата, ассоциирующая с данной ячейкой.*/
    val date: Calendar

    /** Является ли ячейка первым элементом в блоке.*/
    val isFirstItem: Boolean
}