package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate

import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.adapter.CalendarReloadingProvider

/**
 * Делегат для дозагрузки данных в календарь.
 *
 * @author mb.kruglova
 */
internal class CalendarReloadingDelegate(
    private val provider: CalendarReloadingProvider,
    private val layoutManager: LinearLayoutManager?
) {

    /** Свойство дозагрузки календаря. */
    internal var isReloading = false

    /**
     * Дозагрузить данные в адаптер.
     *
     * @param verticalScrollAmount величина вертикальной прокрутки.
     * @param minViewsToPage минимальное количество элементов, отображаемых календарем, до начала дозагрузки данных.
     * */
    internal fun reloadData(
        verticalScrollAmount: Int,
        minViewsToPage: Int
    ) {
        if (verticalScrollAmount != 0) {
            val isNextPage = verticalScrollAmount > 0

            if (isNextPage && layoutManager.isNeedLoadPage(true, minViewsToPage) && !isReloading) {
                isReloading = true
                provider.performCalendarReloading(true)
            }

            if (!isNextPage && layoutManager.isNeedLoadPage(false, minViewsToPage) && !isReloading) {
                isReloading = true
                provider.performCalendarReloading(false)
            }
        }
    }

    /** Проверить, нужно ли догружать данные для календаря. */
    private fun LinearLayoutManager?.isNeedLoadPage(
        isNextPage: Boolean,
        minViewsToPage: Int
    ): Boolean {
        if (this == null) return false
        val position = if (isNextPage) {
            val count: Int = this.itemCount
            count - (this.findLastVisibleItemPosition())
        } else {
            this.findFirstVisibleItemPosition()
        }
        return position <= minViewsToPage
    }
}