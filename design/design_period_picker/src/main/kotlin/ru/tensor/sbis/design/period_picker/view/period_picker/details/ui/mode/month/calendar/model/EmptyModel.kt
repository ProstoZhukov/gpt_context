package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model

import java.util.Calendar

/**
 * Пустая ячейка.
 * @property date описывает первый день месяца, ближайший к этой ячейке.
 *
 * @author mb.kruglova
 */
internal class EmptyModel(override val date: Calendar) : DayItemModel {
    override val isFirstItem = false
}