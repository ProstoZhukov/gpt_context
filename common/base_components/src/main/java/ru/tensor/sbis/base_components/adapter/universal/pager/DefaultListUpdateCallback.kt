package ru.tensor.sbis.base_components.adapter.universal.pager

import androidx.recyclerview.widget.ListUpdateCallback

/**
 * Класс для [androidx.recyclerview.widget.DiffUtil.DiffResult.dispatchUpdatesTo]
 * @param adapter адаптер, который уведомляется при изменении контента
 *
 * @author sa.nikitin, am.boldinov
 */
open class DefaultListUpdateCallback(
    private val adapter: androidx.recyclerview.widget.RecyclerView.Adapter<out androidx.recyclerview.widget.RecyclerView.ViewHolder>
) : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(position, count, payload)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }
}