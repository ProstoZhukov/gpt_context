package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import ru.tensor.sbis.design.period_picker.R
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.ShortPeriodBackgroundDrawable

/**
 * Базовый ViewHolder элементов выбора периода.
 *
 * @author mb.kruglova
 */
internal abstract class BaseViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /** Обводка выбранного периода. */
    protected val selectionView: View = itemView.findViewById<View?>(R.id.selection_view).apply {
        background = ShortPeriodBackgroundDrawable(itemView.context)
    }

    /** Корневой контейнер. */
    protected val rootContainer: ConstraintLayout = itemView.findViewById(R.id.root_container)

    protected var currentPosition: Int = 0
    protected lateinit var listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    protected var isSelected = false

    /** Связать вью с данными. */
    abstract fun bind(
        item: T,
        position: Int,
        listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    )

    /** Настроить обводку выбранного периода. */
    abstract fun setSelection()

    /** Сбросить обводку выбранного периода. */
    abstract fun resetSelection()

    /** Проверить, есть ли обводка выбранного периода. */
    protected fun isSelection(): Boolean = isSelected
}