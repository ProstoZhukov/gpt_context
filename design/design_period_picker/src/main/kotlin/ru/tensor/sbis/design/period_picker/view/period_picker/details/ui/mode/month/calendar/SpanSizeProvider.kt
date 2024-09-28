package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar

/**
 * Поставщик интервалов, занимаемых каждым элементом в GridLayoutManager.
 *
 * @author mb.kruglova
 */
internal interface SpanSizeProvider {
    fun getSpanSize(position: Int): Int
}