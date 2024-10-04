package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.design_menu.QuickActionMenuItem
import ru.tensor.sbis.design.design_menu.viewholders.ViewType
import ru.tensor.sbis.design.design_menu.databinding.QuickActionMenuItemBinding

private const val QUICK_ACTION_MENU_ITEM_VIEW_TYPE = 1

/**
 * Адаптер списка меню быстрых действий.
 *
 * @author ra.geraskin
 */
internal class QuickActionMenuAdapter(private val styleHolder: QuickActionMenuStyleHolder) :
    RecyclerView.Adapter<QuickActionMenuItemViewHolder>() {

    private var items = mutableListOf<QuickActionMenuItem>()

    /** Слушатель кликов для элементов. */
    var clickListener: ((item: QuickActionMenuItem) -> Unit)? = null

    /** Задать список элементов. */
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: Iterable<QuickActionMenuItem>) {
        if (newItems.toList().isEmpty()) return
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = QUICK_ACTION_MENU_ITEM_VIEW_TYPE

    override fun onCreateViewHolder(parent: ViewGroup, @ViewType viewType: Int): QuickActionMenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuickActionMenuItemBinding.inflate(inflater, parent, false)
        return QuickActionMenuItemViewHolder(binding, styleHolder)
    }

    override fun onBindViewHolder(holder: QuickActionMenuItemViewHolder, position: Int) =
        holder.bind(items[position], clickListener)

    override fun getItemCount() = items.count()

}