package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedCompanyItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedFolderItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItemWithImage
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItemsViewConfiguration
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedPersonItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedTextItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.createDefaultConfiguration
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedCompanyItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedFolderItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedItemTextViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedItemWithImageViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedPersonItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory.DefaultSelectedItemViewHolderFactory
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory.SelectedItemViewHolderFactory

/**
 * Адаптер панели выбранных элементов
 *
 * @author us.bessonov
 */
internal class SelectedItemsAdapter : RecyclerView.Adapter<SelectedItemViewHolder<*>>() {

    private var parent: RecyclerView? = null

    private val items = mutableListOf<SelectedItem>()

    lateinit var itemFactory: SelectedItemViewHolderFactory

    lateinit var config: SelectedItemsViewConfiguration

    /** @SelfDocumented */
    fun reload(data: List<SelectedItem>) {
        val diffResult = DiffUtil.calculateDiff(SelectedItemsDiffCallback(items, data))
        items.clear()
        items.addAll(data)
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
                parent?.apply {
                    val end = items.size - 1
                    if (position == end || count == items.size) {
                        if (isLaidOut) scrollToPosition(end) else post { scrollToPosition(end) }
                    }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder<*> {
        items.map { it::class.java }
            .first { it.hashCode() == viewType }
            .let { return itemFactory.createViewHolder(it, config.maxItemWidth) }
    }

    override fun onBindViewHolder(holder: SelectedItemViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is SelectedCompanyItemViewHolder -> holder.setData(item as SelectedCompanyItem)
            is SelectedFolderItemViewHolder -> holder.setData(item as SelectedFolderItem)
            is SelectedItemTextViewHolder -> holder.setData(item as SelectedTextItem)
            is SelectedItemWithImageViewHolder -> holder.setData(item as SelectedItemWithImage)
            is SelectedPersonItemViewHolder -> holder.setData(item as SelectedPersonItem)
            else -> error("Unknown view holder type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position]::class.java.hashCode()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        parent = recyclerView
        config = createDefaultConfiguration(recyclerView.context)
        itemFactory = DefaultSelectedItemViewHolderFactory(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        parent = null
    }

}