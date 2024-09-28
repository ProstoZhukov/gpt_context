package ru.tensor.sbis.list.view.binding

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.BR
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.item.merge.MergeableItem

/**
 * Реализация с использованием Android Data binding с использованием [factory] для создания [View] ячейки.
 * Кастомны действия по событиям [ViewHolder] можно задать через [callback]. При этом, действие
 * [ViewHolderCallback.afterBindToViewHolder] будет вызвано только если в [ViewHolder] выполнил биниднг модели, отличной
 * от той, для которой он уже в нем выполнялся.
 *
 * @param DATA тип данных для биндинга.
 */
open class DataBindingViewHolderHelper<DATA : Any>(
    private val factory: ViewFactory,
    private val callback: ViewHolderCallback = DoNothing()
) :
    ViewHolderHelper<DATA, DataBindingViewHolder> {

    constructor(@LayoutRes layoutId: Int, callback: ViewHolderCallback = DoNothing()) : this(
        LayoutIdViewFactory(
            layoutId
        ), callback
    )

    override fun bindToViewHolder(
        data: DATA,
        viewHolder: DataBindingViewHolder
    ) {
        val currentIsMergeable = viewHolder.getVariable(BR.viewModel) as? MergeableItem<DATA>
        if (currentIsMergeable != null && currentIsMergeable.areTheSame(data)) {
            currentIsMergeable.mergeFrom(data)
        } else {
            viewHolder.setVariable(BR.viewModel, data) {
                viewHolder.executePendingBindings()
                callback.afterBindToViewHolder(data, viewHolder)
            }
        }
    }

    override fun createViewHolder(parentView: ViewGroup): DataBindingViewHolder {
        val view = factory.createView(parentView)
        val dataBindingViewHolder = DataBindingViewHolder(view)
        callback.afterCreateViewHolder(parentView, view, dataBindingViewHolder)
        return dataBindingViewHolder
    }

    override fun getViewHolderType(): Any = factory.getType()
}