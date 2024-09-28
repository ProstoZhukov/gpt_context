package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView

/**
 * Базовый класс вьюхолдера элемента.
 * Позволяет получать [View] для элемента и определять ширину, с учётом данных, для проверки возможности размещения
 * [View] в контейнере
 *
 * @author us.bessonov
 */
internal abstract class SelectedItemViewHolder<ITEM : SelectedItem>(
    @Px
    maxItemWidth: Int,
    view: SelectedItemView
) : RecyclerView.ViewHolder(view) {

    private var data: ITEM? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    internal val item: ITEM
        get() = data!!

    init {
        view.widthLimit = maxItemWidth
    }

    fun setData(data: ITEM?) {
        this.data = data
        bind()
    }

    @CallSuper
    open fun recycle() {
        data = null
    }

    protected abstract fun bind()
}
