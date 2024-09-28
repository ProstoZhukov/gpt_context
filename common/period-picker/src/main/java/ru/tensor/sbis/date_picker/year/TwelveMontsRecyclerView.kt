package ru.tensor.sbis.date_picker.year

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.date_picker.DatePickerRecyclerView
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.year.items.MonthVM

private const val NUMBER_OF_COLUMNS = 3

/**
 * View для вывода сетки, состоящей из 12 месяцев
 *
 * @author mb.kruglova
 */
class TwelveMonthsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DatePickerRecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)
    }

    override fun showData(data: List<Any>) {
        adapter = TwelveMonthsAdapter(data)
    }

    class TwelveMonthsAdapter(months: List<Any>) : ViewModelAdapter() {
        init {
            cell<MonthVM>(R.layout.item_month_with_checkbox)
            reload(months)
        }
    }
}