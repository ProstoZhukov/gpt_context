package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.QuantumBackgroundDrawable
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel

/**
 * ViewHolder ячейки с квантом.
 *
 * @author mb.kruglova
 */
internal abstract class QuantumViewHolder<T : QuantumItemModel>(private val view: View) :
    RecyclerView.ViewHolder(view) {

    /** Метод для связывания данных с ViewHolder. */
    internal fun bind(model: T) {
        setTitle(model)
        setBackground(model)
    }

    /** @SelfDocumented */
    abstract fun setTitle(model: T)

    /** @SelfDocumented */
    private fun setBackground(model: T) {
        view.background = QuantumBackgroundDrawable(view.context).apply {
            quantumType = model.quantumSelection.quantumType
            drawableType = model.quantumSelection.drawableType
        }
    }
}