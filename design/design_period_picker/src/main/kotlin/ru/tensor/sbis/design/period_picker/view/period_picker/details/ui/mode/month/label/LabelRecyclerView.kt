package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.label

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import ru.tensor.sbis.design.R as RDesign

/**
 * View для вывода меток в шапке выбора периода.
 *
 * @author mb.kruglova
 */
internal class LabelRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        val weekDays = resources.getStringArray(RDesign.array.day_of_week_short)
        val labels = weekDays.map {
            LabelModel(it.lowercase(), weekDays.indexOf(it) >= 5)
        }

        val labelAdapter = LabelAdapter()
        labelAdapter.reload(labels)

        layoutManager = GridLayoutManager(context, Calendar.DAY_OF_WEEK)
        adapter = labelAdapter
        isNestedScrollingEnabled = false
    }
}