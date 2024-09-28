package ru.tensor.sbis.date_picker

/**
 * Событие подтверждения выбранного периода или даты
 *
 * @author mb.kruglova
 */
data class PeriodPickedEvent(val period: Period, val resultReceiverId: String = "")

/**
 * Событие нажатия на кнопку сброса
 *
 * @author mb.kruglova
 */
class ResetButtonClickedEvent