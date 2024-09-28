package ru.tensor.sbis.list.view.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.view.SelectionManager
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder
import ru.tensor.sbis.list.view.decorator.stiky_header.StickyHeaderInterface
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.ItemClickListenerFactory
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.ProgressItem


/**
 * Адаптер делегирует вызовы методов работы с ViewHolder элементам списка, а так же делает некоторую подготовительную
 * работу с View ячеек для приведения к стандарту.
 *
 * @property delegate
 */
internal class SbisAdapter(
    private val clickListenerFactory: ItemClickListenerFactory = ItemClickListenerFactory(),
    private val delegate: SbisAdapterDelegate = SbisAdapterDelegate()
) : RecyclerView.Adapter<ViewHolder>(),
    SbisAdapterApi,
    StickyHeaderInterface {

    private val dataBindingViewHolders = mutableSetOf<DataBindingViewHolder>()
    private lateinit var sbisList: SelectionManager
    var lastHighlightedItemPosition = NO_ITEM_TO_HIGHLIGHT
    var highlightSelection = false
        set(value) {
            field = value
            sbisList.highlightItem(lastHighlightedItemPosition)
        }

    var clickListener: (Any) -> Unit = {}

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    //region RecyclerView.Adapter
    @Synchronized
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    @Synchronized
    override fun getItemViewType(position: Int) = delegate.getItemViewType(position)

    @Synchronized
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.onBindViewHolder(holder, position)
        makeupView(holder.itemView, position)

        if (holder is DataBindingViewHolder) dataBindingViewHolders.add(holder)
    }

    @Synchronized
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val itemPayload = payloads.firstOrNull { it is ItemPayload }
        if (itemPayload != null) {
            delegate.merge(holder, (itemPayload as ItemPayload).item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    @Synchronized
    override fun onViewRecycled(holder: ViewHolder) {
        delegate.onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is DataBindingViewHolder) holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is DataBindingViewHolder) holder.onDetach()
    }

    @Synchronized
    override fun getItemCount() = delegate.getItemCount()
    //endregion

    //region StickyHeaderInterface
    @Synchronized
    override fun runWithHeaderPosition(
        forPosition: Int,
        parent: ViewGroup,
        func: (Int, View) -> Unit
    ) {
        val headerPos = delegate.getStickyHeaderPos(forPosition)

        if (headerPos == NO_HEADER) return

        val currentHeader = createViewForItemInPosition(headerPos, parent)
        func(headerPos, currentHeader)
    }

    @Synchronized
    override fun isSticky(position: Int) = delegate.isSticky(position)
    //endregion

    //region public
    @Synchronized
    fun setItems(data: ListData) {
        doActionAndHandleSelection { delegate.setItemsAndNotify(data, this) }
    }

    @Synchronized
    fun swap(firstElementIndex: Int, secondElementIndex: Int) {
        delegate.swap(firstElementIndex, secondElementIndex)
    }

    @Synchronized
    fun notifyItemChanged(position: Int, list: List<AnyItem>) {
        doActionAndHandleSelection {
            delegate.replaceItems(list)
            notifyItemChanged(position)
        }
    }


    @Synchronized
    fun notifyItemInserted(position: Int, list: List<AnyItem>) {
        doActionAndHandleSelection {
            delegate.replaceItems(list)
            notifyItemInserted(position)
        }
    }

    @Synchronized
    fun notifyItemRemoved(position: Int, list: List<AnyItem>) {
        doActionAndHandleSelection {
            delegate.replaceItems(list)
            notifyItemRemoved(position)
        }
    }

    @Synchronized
    fun notifyItemRangeInserted(
        positionStart: Int,
        itemCount: Int,
        list: List<AnyItem>
    ) {
        doActionAndHandleSelection {
            delegate.replaceItems(list)
            notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    @Synchronized
    fun notifyItemRangeRemoved(
        positionStart: Int,
        itemCount: Int,
        list: List<AnyItem>
    ) {
        doActionAndHandleSelection {
            delegate.replaceItems(list)
            notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    @Synchronized
    fun addLast(tem: AnyItem) {
        if (delegate.addLastWithCheck(tem)) notifyItemInserted(delegate.getLastIndex())
    }

    @Synchronized
    fun removeLast(itemProgress: ProgressItem) {
        delegate.removeIfLast(itemProgress) { indexOfRemoved ->
            notifyItemRemoved(indexOfRemoved)
        }
    }

    @Synchronized
    fun addFirst(tem: AnyItem) {
        delegate.addFirst(tem)
        notifyItemInserted(0)
    }

    @Synchronized
    fun removeFirst(itemProgress: ProgressItem) {
        delegate.removeFirst(itemProgress) {
            notifyItemRemoved(0)
        }
    }

    fun registerSelectionObserver(sbisList: SelectionManager) {
        this.sbisList = sbisList
    }

    fun setShouldThrottleItemClicksSeparately(throttleSeparately: Boolean) {
        clickListenerFactory.shouldThrottleItemClicksSeparately = throttleSeparately
    }

    internal fun setItemClicksThrottleInterval(interval: Long) {
        clickListenerFactory.interval = interval
    }

    fun destroyDataBindingViewHolders() {
        dataBindingViewHolders.forEach { it.destroy() }
        dataBindingViewHolders.clear()
    }

    @Synchronized
    fun doWithItemPosition(predicate: (AnyItem) -> Boolean, action: (Int) -> Unit) {
        delegate.doWithItemPosition(predicate, action)
    }

    fun saveState(state: Bundle) {
        state.putInt(LAST_HIGHLIGHTED_ITEM_POSITION, lastHighlightedItemPosition)
    }

    fun getRestore(bundle: Bundle) {
        lastHighlightedItemPosition = bundle.getInt(LAST_HIGHLIGHTED_ITEM_POSITION)
    }

    @Synchronized
    override fun getItem(topChildPosition: Int) = delegate.getItem(topChildPosition)
    //endregion

    //region private
    /**
     * Обработка View созданной [ViewHolder], устанавливается фон, кликабельность, обработка клика и прочее согласно
     * настройкам элемента.
     */
    private fun makeupView(view: View, position: Int) {
        if (delegate.getUseCustomListeners(position)) return

        if (delegate.isClickable(position)) {
            val clickAction = delegate.clickAction(position)
            view.setOnClickListener(clickListenerFactory.createClickListener {
                clickAction()
                if (highlightSelection && delegate.isHighlightable(position)) {
                    sbisList.highlightItem(position)
                    lastHighlightedItemPosition = position
                }
            })
            val longClickAction = delegate.longClickAction(position)
            view.setOnLongClickListener {
                longClickAction()
                true
            }

            view.isEnabled = true
        } else {
            view.setOnClickListener(null)
            view.isClickable = false
            view.isEnabled = false
        }
    }

    private fun createViewForItemInPosition(
        position: Int,
        parent: ViewGroup
    ): View {
        val viewHolder = createViewHolder(parent, getItemViewType(position))

        onBindViewHolder(viewHolder, position)
        return viewHolder.itemView
    }

    private fun doActionAndHandleSelection(action: () -> Unit) {
        var itemToHighlight: AnyItem? = null

        try {
            if (highlightSelection && lastHighlightedItemPosition != NO_ITEM_TO_HIGHLIGHT) {
                itemToHighlight = delegate.getItem(lastHighlightedItemPosition)
            }
        } catch (e: Exception) {
            //ignore
        }

        action()

        if (highlightSelection && itemToHighlight != null) {
            lastHighlightedItemPosition = delegate.getItemPosition(itemToHighlight)
            if (lastHighlightedItemPosition == NO_ITEM_TO_HIGHLIGHT) sbisList.cleanSelection()
            else sbisList.highlightItem(lastHighlightedItemPosition)
        }
    }
    //endregion
}

internal const val NO_ITEM_TO_HIGHLIGHT = -1
private const val LAST_HIGHLIGHTED_ITEM_POSITION = "LAST_HIGHLIGHTED_ITEM_POSITION"