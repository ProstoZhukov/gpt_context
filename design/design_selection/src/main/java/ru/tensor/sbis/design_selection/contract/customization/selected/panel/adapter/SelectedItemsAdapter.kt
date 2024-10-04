package ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Адаптер списка выбранных элементов.
 *
 * @property itemsCustomization кастомизатор выбранных элементов.
 * @property viewHolderHelpers набор [ViewHolderHelper] для создания вью-холдеров списка.
 *
 * @author vv.chekurda
 */
class SelectedItemsAdapter(
    private val itemsCustomization: SelectedItemsCustomization<SelectionItem>,
    private val viewHolderHelpers: Map<Any, ViewHolderHelper<SelectionItem, ViewHolder>>
) : RecyclerView.Adapter<ViewHolder>() {

    private var parent: RecyclerView? = null

    private val items = mutableListOf<SelectionItem>()

    /**
     * Установить список элементов.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<SelectionItem>) {
        if (data.isEmpty()) {
            val isChanged = items.isNotEmpty()
            items.clear()
            if (isChanged) notifyDataSetChanged()
        } else {
            val diffResult = DiffUtil.calculateDiff(SelectedItemsDiffCallback(items, data))
            items.clear()
            items.addAll(data)
            diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {
                    notifyItemRangeInserted(position, count)
                    parent?.apply {
                        val end = data.lastIndex
                        if (isLaidOut) scrollToPosition(end) else post { scrollBy(Int.MAX_VALUE, 0) }
                    }
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyItemRangeRemoved(position, count)
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }

                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    notifyItemRangeChanged(position, count, payload)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        getHelper(viewType = viewType).createViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        getHelper(position = position).bindToViewHolder(item, holder)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val type = itemsCustomization.getViewHolderType(items[position])
        return viewHolderHelpers.keys.indexOf(type)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        parent = recyclerView
        recyclerView.recycledViewPool.apply {
            repeat(viewHolderHelpers.size) {
                setMaxRecycledViews(it, 50)
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        parent = null
    }

    private fun getHelper(viewType: Int? = null, position: Int? = null): ViewHolderHelper<SelectionItem, ViewHolder> {
        val key = when {
            viewType != null -> viewHolderHelpers.keys.toList()[viewType]
            position != null -> itemsCustomization.getViewHolderType(items[position])
            else -> throw IllegalStateException("Отсутствует позиция или тип")
        }
        return viewHolderHelpers[key]!!
    }
}