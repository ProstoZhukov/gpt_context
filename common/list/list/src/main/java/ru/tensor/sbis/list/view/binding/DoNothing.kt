package ru.tensor.sbis.list.view.binding

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Колбек-заглушка, ничего не делает.
 */
class DoNothing : ViewHolderCallback {

    override fun afterBindToViewHolder(
        data: Any,
        viewHolder: DataBindingViewHolder
    ) = Unit

    override fun afterCreateViewHolder(
        parentView: ViewGroup,
        createdView: View,
        viewHolder: RecyclerView.ViewHolder
    ) = Unit
}