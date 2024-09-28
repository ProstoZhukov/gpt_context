package ru.tensor.sbis.design.cylinder.picker.value

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cylinder.picker.cylinder.CylinderViewHolder

/**
 * @author Subbotenko Dmitry
 */

internal class CylinderValuePickerAdapter<TYPE>(
    var values: Collection<TYPE>,
    private val comparator: Comparator<TYPE>,
    private val toStringConverter: (TYPE) -> String
) :
    RecyclerView.Adapter<CylinderViewHolder>() {

    var currentValue: TYPE? = null
        set(value) {
            field = value
            actualValuesList = values.toMutableList()
                .apply { if (find { comparator.compare(it, value) == 0 } == null && value != null) add(value) }
                .sortedWith(comparator)
            notifyDataSetChanged()
        }

    private var actualValuesList: List<TYPE> = values.sortedWith(comparator).toList()

    fun getValueForPosition(position: Int): TYPE? =
        if (position - 2 >= 0 && position - 2 < actualValuesList.size) actualValuesList[position - 2]
        else null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CylinderViewHolder =
        CylinderViewHolder(parent)

    override fun getItemCount(): Int = actualValuesList.size + 4

    override fun onBindViewHolder(holder: CylinderViewHolder, position: Int) {
        holder.bind(getValueForPosition(position)?.let { toStringConverter(it) } ?: "")
    }

    fun getPositionForValue(value: TYPE) = actualValuesList.indexOfFirst { comparator.compare(it, value) >= 0 } + 2
}