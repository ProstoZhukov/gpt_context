package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.adapter.CalendarReloadingProvider
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders.DayViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders.EmptyViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders.MonthLabelViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayItemModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.EmptyModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.MonthLabelModel
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar

/**
 * Адаптер, который хранит элементы календаря и обеспечивает их синхронизацию.
 *
 * @author mb.kruglova
 */
internal class MonthModePeriodPickerAdapter(
    var listener: CalendarListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CalendarReloadingProvider,
    SpanSizeProvider {

    private var items = mutableMapOf<Int, DayItemModel>()
    private var isEnabled: Boolean = true

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            DayViewHolder.ITEM_TYPE -> DayViewHolder.create(parent, listener, isEnabled)
            MonthLabelViewHolder.ITEM_TYPE -> MonthLabelViewHolder.create(parent, listener, isEnabled)
            else -> EmptyViewHolder.create(parent)
        }
        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> holder.bind(items[position] as DayModel)
            is MonthLabelViewHolder -> holder.bind(items[position] as MonthLabelModel)
            is EmptyViewHolder -> holder.bind()
        }
    }

    override fun getSpanSize(position: Int): Int {
        return when (items[position]) {
            is DayModel, is EmptyModel -> 1
            else -> Calendar.DAY_OF_WEEK
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DayModel -> DayViewHolder.ITEM_TYPE
            is MonthLabelModel -> MonthLabelViewHolder.ITEM_TYPE
            else -> EmptyViewHolder.ITEM_TYPE
        }
    }

    override fun performCalendarReloading(isNextPage: Boolean) {
        listener?.onReloadCalendar(isNextPage)
    }

    /** Обновить данные для адаптера. */
    @SuppressLint("NotifyDataSetChanged")
    internal fun update(newItems: List<DayItemModel>) {
        items.clear()
        items.addItems(0, newItems)
        notifyDataSetChanged()
    }

    /** Дозагрузить данные для адаптера. */
    internal fun reload(newItems: List<DayItemModel>, addToEnd: Boolean) {
        if (addToEnd) {
            val index = items.size
            items.addItems(index, newItems)
            notifyItemRangeInserted(index, newItems.size)
        } else {
            val newItemMap = mutableMapOf<Int, DayItemModel>()
            newItemMap.addItems(0, newItems)
            newItemMap.addItems(newItems.size, items.values.toList())
            items = newItemMap
            notifyItemRangeInserted(0, newItems.size)
        }
    }

    /** Добавить элементы. */
    private fun MutableMap<Int, DayItemModel>.addItems(index: Int, newItems: List<DayItemModel>) {
        var i = index
        newItems.forEach {
            this[i] = it
            i++
        }
    }

    /** Получить элемент по позиции. */
    private fun getItemByPosition(position: Int): DayItemModel? = items[position]

    /** Получить дату по позиции. */
    internal fun getDateByPosition(position: Int): Calendar {
        return getItemByPosition(position)?.date ?: MIN_DATE
    }

    /** Получить дату для шапки календаря по позиции. */
    internal fun getHeaderDateByPosition(position: Int): Calendar {
        if (position < 0) return MIN_DATE
        var item = getItemByPosition(position)
        if (item is MonthLabelModel && position > 0) {
            item = getItemByPosition(position - 1)
        }
        return item?.date ?: MIN_DATE
    }

    /** Получить месяц в календаре. */
    internal fun getMonthByPosition(position: Int, defaultMonth: Int): Int {
        if (position < 0) return defaultMonth
        val item = getItemByPosition(position)
        return when {
            item == null -> defaultMonth
            item is MonthLabelModel && position > 0 -> item.date.month - 1
            else -> item.date.month
        }
    }

    /** Получить год в календаре. */
    internal fun getYearByPosition(position: Int): Int {
        return getItemByPosition(position)?.date?.year ?: MIN_DATE.year
    }

    /** Получить позицию первого элемента месяца. */
    internal fun getFirstMonthPosition(month: Int, year: Int, isCompact: Boolean): Int {
        val item = items.filterValues {
            it.isFirstItem && it.date.month == month && it.date.year == year
        }

        return when {
            item.keys.isEmpty() -> 0
            isCompact -> item.keys.first() + 1
            else -> item.keys.first()
        }
    }

    /** Получить позицию дня в календаре. */
    internal fun getPosition(date: Calendar): Int {
        val item = items.filterValues {
            !it.isFirstItem &&
                it.date.month == date.month &&
                it.date.year == date.year &&
                it.date.dayOfMonth == date.dayOfMonth
        }

        val day = item.filterValues { it is DayModel }

        return if (day.keys.isNotEmpty()) day.keys.first() else 0
    }

    /** Получить последнюю позицию в календаре. */
    internal fun getLastPosition() = itemCount - 1

    /** Настроить доступность для взаимодействия. */
    internal fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /** Получить значение доступности компонента для взаимодействия. */
    internal fun getEnabled() = isEnabled
}