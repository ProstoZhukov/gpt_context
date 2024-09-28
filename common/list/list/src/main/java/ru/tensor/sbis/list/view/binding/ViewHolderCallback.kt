package ru.tensor.sbis.list.view.binding

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * Колбек методов [ViewHolder], вызывются после выполнения соответствующих методов.
 */
interface ViewHolderCallback {

    /**@SelfDocumented**/
    fun afterBindToViewHolder(
        data: Any,
        viewHolder: DataBindingViewHolder
    ) = Unit

    /**@SelfDocumented**/
    fun afterCreateViewHolder(
        parentView: ViewGroup,
        createdView: View,
        viewHolder: ViewHolder
    ) = Unit
}