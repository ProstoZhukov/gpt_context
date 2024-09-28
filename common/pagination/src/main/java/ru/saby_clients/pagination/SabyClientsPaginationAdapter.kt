package ru.saby_clients.pagination

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import ru.tensor.sbis.base_components.adapter.AbstractListAdapter
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Базовая реализация адаптера списка для односторонней пагинации
 */
open class SabyClientsPaginationAdapter<DM : Any> : AbstractListAdapter<DM, AbstractViewHolder<DM>>() {

    companion object {
        private const val HOLDER_EMPTY = -3
        private const val HOLDER_PROGRESS = -4
    }

    /**
     * Флаг обозначающий необходимость показать ячейку с прогресс баром в списке
     */
    internal var showLoadingProgress: Boolean = false
        set(value) {
            val changed = value != field
            field = value
            if (changed) {
                if (value) notifyItemInserted(content.size)
                else notifyItemRemoved(content.size)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<DM> =
        when (viewType) {
            HOLDER_PROGRESS -> createProgressViewHolder(parent)
            HOLDER_EMPTY -> createEmptyViewHolder(parent)
            else -> AbstractViewHolder(parent)
        }

    override fun onBindViewHolder(holder: AbstractViewHolder<DM>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = if (content.isNotEmpty()) getCount() else 0

    private fun getCount(): Int = content.size + (if (showLoadingProgress) 1 else 0)

    override fun getItem(position: Int): DM? =
        if (position >= 0 && position < content.size) content[position] else null

    override fun getItemViewType(position: Int): Int =
        if (position == content.size && showLoadingProgress) HOLDER_PROGRESS
        else getItemType(getItem(position))

    /**
     * Получение типа ячейки
     */
    @CallSuper
    open fun getItemType(dataModel: DM?): Int = HOLDER_EMPTY

    private fun createProgressViewHolder(parent: ViewGroup): AbstractViewHolder<DM> =
        AbstractViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(RBaseComponents.layout.base_components_progress_list_item, parent, false)
        )

    private fun createEmptyViewHolder(parent: ViewGroup): AbstractViewHolder<DM> =
        AbstractViewHolder(View(parent.context).also {
            it.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        })
}