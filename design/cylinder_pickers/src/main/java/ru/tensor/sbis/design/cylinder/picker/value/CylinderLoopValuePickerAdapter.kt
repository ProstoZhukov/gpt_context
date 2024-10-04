package ru.tensor.sbis.design.cylinder.picker.value

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cylinder.picker.cylinder.CylinderViewHolder
import ru.tensor.sbis.design.cylinder.picker.cylinder.IBindCylinder
import kotlin.math.abs

const val INIT_POSITION = Int.MAX_VALUE / 2

/**
 * Адаптер для [CylinderLoopValuePicker].
 *
 * @author ae.noskov
 */
internal class CylinderLoopValuePickerAdapter<TYPE>(
    private var values: Collection<TYPE>,
    private val comparator: Comparator<TYPE>,
    private val bind: (IBindCylinder, TYPE) -> Unit
) :
    RecyclerView.Adapter<CylinderViewHolder>() {

    /** @SelfDocumented */
    var currentValue: TYPE? = null

    var actualValuesList: List<TYPE> = values.sortedWith(comparator).toList()
        private set

    /** @SelfDocumented */
    fun getValueForPosition(position: Int): TYPE? = actualValuesList[getAdjustedPosition(position)]

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CylinderViewHolder =
        CylinderViewHolder(parent)

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: CylinderViewHolder, position: Int) {
        val type = getValueForPosition(position)
        if (type != null)
            bind(holder, type)
        else
            holder.bind("")
    }

    /** @SelfDocumented */
    fun getAdjustedPosition(position: Int): Int {
        val size = if (values.isEmpty()) 1 else values.size
        var p = (position - INIT_POSITION) % size
        if (p < 0) {
            p = size - abs(p)
        }
        return p
    }

    /** @SelfDocumented */
    fun getPositionForValue(value: TYPE, currentTop: Int): Int {
        val size = if (values.isEmpty()) 1 else values.size
        var res = actualValuesList.indexOfFirst { it!! == value }
        res += INIT_POSITION + ((currentTop - INIT_POSITION) / size) * size
        return res
    }

    /** Установить список. */
    fun collection(list: List<TYPE>) {
        values = list
        actualValuesList = values.toMutableList().sortedWith(comparator)
        notifyDataSetChanged()
    }
}