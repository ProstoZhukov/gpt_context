package ru.tensor.sbis.date_picker

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

const val NUMBER_OF_COLUMNS = Calendar.DAY_OF_WEEK

/**
 * @author mb.kruglova
 */
internal class DatePickerLayoutManager(
    context: Context,
    private val spanSizeProvider: SpanSizeProvider,
    private val onLayoutCompletedCallback: () -> Unit
) : GridLayoutManager(context, NUMBER_OF_COLUMNS) {

    private val dayNumberWidth = context.resources.getDimension(R.dimen.date_picker_day_size).toInt()
    private val dayNumbersLeftOffset =
        ((context.resources.getDimension(R.dimen.date_picker_width) - NUMBER_OF_COLUMNS * dayNumberWidth) / 2).toInt()

    init {
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return spanSizeProvider.getSpanSize(position)
            }
        }
    }

    override fun layoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        /**
         * Для календарной сетки с днями отступы элементов рассчитываются вручную,
         * т.к. по умолчанию [GridLayoutManager] выравнивает столбцы по левому краю
         * (по макету требуется выравнивать по центру)
         */
        if (child.id == R.id.date_picker_item_day || child.id == R.id.date_picker_item_month_day_empty_layout) {
            val dayOfWeek = (child.layoutParams as LayoutParams).spanIndex
            child.layout(
                dayNumbersLeftOffset + dayNumberWidth * dayOfWeek,
                top,
                dayNumbersLeftOffset + dayNumberWidth * (dayOfWeek + 1),
                bottom
            )
        } else {
            super.layoutDecoratedWithMargins(child, left, top, right, bottom)
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        onLayoutCompletedCallback()
    }
}
