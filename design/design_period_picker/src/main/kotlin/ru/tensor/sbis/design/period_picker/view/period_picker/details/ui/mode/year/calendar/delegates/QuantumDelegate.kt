package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel

/**
 * Базовый класс делегата создания элемента списка квантов.
 *
 * @author mb.kruglova
 */
internal abstract class QuantumDelegate<in R : RecyclerView.ViewHolder, in T : QuantumItemModel> {
    /** Тип вью статуса */
    abstract val viewType: Int

    /** @see [RecyclerView.Adapter.onCreateViewHolder] */
    abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    /** @see [RecyclerView.Adapter.onBindViewHolder] */
    abstract fun onBindViewHolder(holder: R, item: T)

    /** @see [RecyclerView.Adapter.onBindViewHolder] */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, items: List<QuantumItemModel>, position: Int) {
        @Suppress("UNCHECKED_CAST")
        onBindViewHolder(holder as R, items[position] as T)
    }
}