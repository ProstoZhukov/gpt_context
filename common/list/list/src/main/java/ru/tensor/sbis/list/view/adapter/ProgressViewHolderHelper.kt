package ru.tensor.sbis.list.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.utils.ProgressItemPlace

/**
 * Реализация для создания [RecyclerView.ViewHolder] для элементов отображающий индикатор подгрузки сверху или снизу
 * списка.
 */
internal class ProgressViewHolderHelper : ViewHolderHelper<ProgressItemPlace, ViewHolder> {

    override fun createViewHolder(parentView: ViewGroup) = object : ViewHolder(
        LayoutInflater
            .from(parentView.context)
            .inflate(
                R.layout.list_item_bottom_stub,
                parentView,
                false
            )
    ) {}

    override fun bindToViewHolder(data: ProgressItemPlace, viewHolder: ViewHolder) {
        when (data) {
            ProgressItemPlace.TOP,
            ProgressItemPlace.BOTTOM -> {
                viewHolder.itemView.updateLayoutParams {
                    width = GridLayoutManager.LayoutParams.MATCH_PARENT
                }
            }
            ProgressItemPlace.LEFT,
            ProgressItemPlace.RIGHT -> {
                viewHolder.itemView.updateLayoutParams {
                    width = GridLayoutManager.LayoutParams.WRAP_CONTENT
                    height = GridLayoutManager.LayoutParams.MATCH_PARENT
                }
            }
        }
    }
}