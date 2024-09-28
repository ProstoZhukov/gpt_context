package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate

import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import java.util.Calendar

/**
 * Интерфейс обновления данных в календаре.
 *
 * @author mb.kruglova
 */
internal interface CalendarUpdateDelegate {

    /** Обновить позицию скроллирования. */
    fun updateScrollPosition(scrollDate: Calendar? = null)

    /** Догрузить данные в календарь. */
    fun reloadCalendar(newData: CalendarStorage, addToEnd: Boolean)

    /** Обновить выделение периода в календаре. */
    fun updateSelection(storage: CalendarStorage)

    /** Сбросить выделение периода в календаре. */
    fun resetSelection()

    /** Установить предустановленный период в календаре. */
    fun setPresetSelection(dateFrom: Calendar, dateTo: Calendar)
}