package ru.tensor.sbis.date_picker.month

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.month.items.DayLabelVM
import java.util.*

/**
 * View для вывода заголовков дней недели (с понедельника по воскресенье)
 *
 * @author mb.kruglova
 */
class DayLabelsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = GridLayoutManager(context, Calendar.DAY_OF_WEEK)
        val data = resources.getStringArray(R.array.day_of_week_labels).map { DayLabelVM(it) }
        adapter = DayLabelsAdapter(data)
    }
}