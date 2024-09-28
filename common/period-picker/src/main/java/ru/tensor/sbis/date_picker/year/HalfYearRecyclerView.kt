package ru.tensor.sbis.date_picker.year

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.date_picker.DatePickerRecyclerView
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.year.items.HalfYearVM

/**
 * View для вывода списка полугодий
 *
 * @author mb.kruglova
 */
class HalfYearRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DatePickerRecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = LinearLayoutManager(context)
    }

    override fun showData(data: List<Any>) {
        adapter = HalfYearAdapter(data)
    }

    class HalfYearAdapter(halfYear: List<Any>) : ViewModelAdapter() {
        init {
            cell<HalfYearVM>(R.layout.item_year_half)
            reload(halfYear)
        }
    }
}