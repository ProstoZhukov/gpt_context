package ru.tensor.sbis.date_picker.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.progress.SbisPullToRefresh
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.date_picker.NUMBER_OF_COLUMNS
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.SpanSizeProvider
import ru.tensor.sbis.date_picker.adapter.viewholder.BindingViewHolder
import ru.tensor.sbis.date_picker.adapter.viewholder.DatePickerViewHolder
import ru.tensor.sbis.date_picker.adapter.viewholder.ItemMonthDayEmptyViewHolder
import ru.tensor.sbis.date_picker.adapter.viewholder.ItemMonthDayViewHolder
import ru.tensor.sbis.date_picker.adapter.viewholder.ItemMonthLabelViewHolder
import ru.tensor.sbis.date_picker.free.items.LabelVM
import ru.tensor.sbis.date_picker.free.items.RecentPeriodVM
import ru.tensor.sbis.date_picker.items.BottomStub
import ru.tensor.sbis.date_picker.month.items.DayVM
import ru.tensor.sbis.date_picker.month.items.EmptyVM
import ru.tensor.sbis.date_picker.month.items.MonthLabelVM
import ru.tensor.sbis.date_picker.year.items.YearPeriodsVM

/**
 * Адаптер для основных элементов календарной сетки (год и месяц)
 *
 * @author mb.kruglova
 */
internal class DatePickerAdapter : RecyclerView.Adapter<DatePickerViewHolder>(), SpanSizeProvider {

    private val items = mutableListOf<Any>()

    private val vmAdapter = ViewModelAdapter().apply {
        cell(
            R.layout.item_year_periods,
            areItemsTheSame = { a: YearPeriodsVM, b: YearPeriodsVM ->
                a.label == b.label && a.items == b.items && a.halfYear == b.halfYear && a.quarters == b.quarters
            }
        )
        cell<RecentPeriodVM>(R.layout.item_recent_period)
        cell<LabelVM>(R.layout.item_recent_period_label)
        cell<BottomStub>(R.layout.bottom_stub)
        cell<DayVM>(ItemMonthDayViewHolder.ITEM_TYPE)
        cell<EmptyVM>(ItemMonthDayEmptyViewHolder.ITEM_TYPE)
        cell(
            ItemMonthLabelViewHolder.ITEM_TYPE,
            areItemsTheSame = { a: MonthLabelVM, b: MonthLabelVM -> a.label == b.label }
        )
    }

    /**
     * Не используем DiffUtils для календарной сетки для большего быстродействия
     * (всегда перезагружаем данные полностью)
     */
    fun reload(newItems: List<Any>, refreshLayout: SbisPullToRefresh?) {
        reload(newItems)
        refreshLayout?.isRefreshing = false
    }

    /** @SelfDocumented */
    fun reload(newItems: List<Any>) {
        items.clear()
        items.addAll(newItems)
        vmAdapter.reload(newItems)
        notifyDataSetChanged()
    }

    /**
     * Очищение адаптера от данных
     * очищение данных увеличивает скорость последующего reload
     */
    fun clear() {
        if (itemCount > 0) {
            items.clear()
            vmAdapter.reload(listOf())
            notifyDataSetChanged()
        }
    }

    /**
     * @see [ViewModelAdapter.tryGetItemAt]
     */
    fun tryGetItemAt(index: Int): Any? = vmAdapter.tryGetItemAt(index)

    fun insertItems(newItems: List<Any>, addToBottom: Boolean) {
        val index = if (addToBottom) items.size else 0
        items.addAll(index, newItems)
        vmAdapter.reload(items)
        notifyItemRangeInserted(index, newItems.size)
    }

    override fun getSpanSize(position: Int): Int {
        return when (items[position]) {
            is DayVM, is EmptyVM -> 1
            else -> NUMBER_OF_COLUMNS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatePickerViewHolder {
        return when (viewType) {
            ItemMonthDayViewHolder.ITEM_TYPE -> ItemMonthDayViewHolder.create(parent)
            ItemMonthDayEmptyViewHolder.ITEM_TYPE -> ItemMonthDayEmptyViewHolder.create(parent)
            ItemMonthLabelViewHolder.ITEM_TYPE -> ItemMonthLabelViewHolder.create(parent)
            else -> BindingViewHolder(vmAdapter.onCreateViewHolder(parent, viewType))
        }
    }

    override fun onBindViewHolder(holder: DatePickerViewHolder, position: Int) {
        when (holder) {
            is BindingViewHolder -> vmAdapter.onBindViewHolder(holder.vmAdapterHolder, position)
            is ItemMonthDayViewHolder -> holder.bind(items[position] as DayVM)
            is ItemMonthDayEmptyViewHolder -> holder.bind()
            is ItemMonthLabelViewHolder -> holder.bind(items[position] as MonthLabelVM)
        }
    }

    override fun getItemCount() = vmAdapter.itemCount

    override fun getItemViewType(position: Int) = vmAdapter.getItemViewType(position)

    override fun onViewRecycled(holder: DatePickerViewHolder) {
        holder.recycle()
    }
}