package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate

/**
 * Делегат для установки ключей запроса и результата выбора периода.
 *
 * @author mb.kruglova
 */
internal interface CalendarRequestResultKeysDelegate {

    val requestKey: String

    val resultKey: String
}