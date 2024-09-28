package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * LayoutManager для календаря.
 *
 * @author mb.kruglova
 */
internal class CalendarLayoutManager(
    context: Context,
    private val spanSizeProvider: SpanSizeProvider,
    spanCount: Int = Calendar.DAY_OF_WEEK
) : GridLayoutManager(context, spanCount) {

    init {
        // Установка интервалов, занимаемых каждым элементом.
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return spanSizeProvider.getSpanSize(position)
            }
        }
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
        // Установка высоты ячейки равной ширине ячейки.
        lp.height = ((width - paddingStart - paddingEnd) / spanCount.toFloat()).roundToInt()
        return true
    }
}