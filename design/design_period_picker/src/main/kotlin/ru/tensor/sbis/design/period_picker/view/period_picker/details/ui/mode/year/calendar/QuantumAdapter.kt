package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates.QuantumDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel

/**
 * Адаптер, который хранит элементы квантов календаря и обеспечивает их синхронизацию.
 *
 * @author mb.kruglova
 */
internal class QuantumAdapter(
    private val delegate: QuantumDelegate<*, *>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val quantums: MutableList<QuantumItemModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegate.onCreateViewHolder(parent)
    }

    override fun getItemCount(): Int = quantums.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegate.onBindViewHolder(holder, quantums, position)
    }

    /** @SelfDocumented */
    @SuppressLint("NotifyDataSetChanged")
    internal fun reload(newItems: List<QuantumItemModel>) {
        quantums.clear()
        quantums.addAll(newItems)
        notifyDataSetChanged()
    }
}