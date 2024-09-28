package ru.tensor.sbis.date_picker

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * View для вывода основных элементов календарной сетки (год и месяц)
 *
 * @author mb.kruglova
 */
abstract class DatePickerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    abstract fun showData(data: List<Any>)
}