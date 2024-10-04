package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeItemBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.adapter.CalendarReloadingProvider
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.YearModePeriodPickerViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.YearLabelViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.YearModePeriodPickerModel
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar

/**
 * Адаптер, который хранит элементы календаря и обеспечивает их синхронизацию.
 *
 * @author mb.kruglova
 */
internal class YearModePeriodPickerAdapter(
    internal val listener: CalendarListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CalendarReloadingProvider {

    private val data: MutableList<Any> = mutableListOf()
    private var isEnabled: Boolean = true

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = if (viewType == YearLabelViewHolder.ITEM_TYPE) {
            YearLabelViewHolder.create(parent)
        } else {
            val binding = PeriodPickerYearModeItemBinding.inflate(inflater, parent, false)
            YearModePeriodPickerViewHolder(binding)
        }

        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YearLabelViewHolder -> holder.bind(data[position] as Int)
            is YearModePeriodPickerViewHolder ->
                holder.bind(data[position] as YearModePeriodPickerModel, listener, isEnabled)
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (data[position] is Int) {
            YearLabelViewHolder.ITEM_TYPE
        } else {
            YearModePeriodPickerViewHolder.ITEM_TYPE
        }

    override fun performCalendarReloading(isNextPage: Boolean) {
        listener?.onReloadCalendar(isNextPage)
    }

    /** @SelfDocumented */
    @SuppressLint("NotifyDataSetChanged")
    internal fun update(newItems: List<Any>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }

    /** Дозагрузить данные для адаптера. */
    internal fun reload(newItems: List<Any>, addToEnd: Boolean) {
        val index = if (addToEnd) data.size else 0
        data.addAll(index, newItems)
        notifyItemRangeInserted(index, newItems.size)
    }

    /** Получить год в календаре по позиции. */
    internal fun getYearByPosition(position: Int): Int {
        if (position < 0) return Calendar.getInstance().year
        val item = getItemByPosition(position)
        return when {
            item is Int && position > 0 -> item - 1
            item is Int -> item
            item is YearModePeriodPickerModel -> item.year
            else -> Calendar.getInstance().year
        }
    }

    /** Получить позицию первого элемента года. */
    internal fun getFirstYearPosition(year: Int): Int {
        val item = getYearPosition(year)
        return if (item == -1) 1 else item + 1
    }

    internal fun getYearPosition(year: Int): Int {
        return data.indexOf(year)
    }

    /** Получить последнюю позицию в календаре. */
    internal fun getLastPosition() = itemCount - 1

    /** Настроить доступность для взаимодействия. */
    internal fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /** Получить элемент по позиции. */
    private fun getItemByPosition(position: Int): Any = data[position]
}