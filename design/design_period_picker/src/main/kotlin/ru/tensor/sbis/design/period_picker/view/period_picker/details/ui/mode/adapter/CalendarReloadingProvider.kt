package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.adapter

/**
 * Поставщик дозагрузки календаря.
 *
 * @author mb.kruglova
 */
internal interface CalendarReloadingProvider {

    /** Выполнить догрузку календаря. */
    fun performCalendarReloading(isNextPage: Boolean)
}