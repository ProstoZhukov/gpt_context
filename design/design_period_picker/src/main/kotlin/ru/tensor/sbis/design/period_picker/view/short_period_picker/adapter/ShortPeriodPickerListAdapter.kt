package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerHalfYearItemBinding
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerMonthItemBinding
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerQuarterItemBinding
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerYearItemBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.BaseViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.HalfYearViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.MonthViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.QuarterViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.YearViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem.*

/**
 * Адаптер, который хранит элементы периодов и обеспечивает их синхронизацию.
 *
 * @author mb.kruglova
 */
internal class ShortPeriodPickerListAdapter(
    private val listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit,
    private val selection: PeriodPickerParams?,
    private val isEnabled: Boolean,
    private val displayedRange: SbisPeriodPickerRange?
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val data: MutableList<ShortPeriodPickerItem> = mutableListOf()

    companion object {
        internal const val TYPE_MONTH = 0
        internal const val TYPE_QUARTER = 1
        internal const val TYPE_HALF_YEAR = 2
        internal const val TYPE_YEAR = 3
    }

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val holder = when (viewType) {
            TYPE_MONTH -> {
                val binding: ShortPeriodPickerMonthItemBinding =
                    ShortPeriodPickerMonthItemBinding.inflate(inflater, parent, false)
                MonthViewHolder(binding.root, binding, isEnabled, selection, displayedRange)
            }
            TYPE_QUARTER -> {
                val binding: ShortPeriodPickerQuarterItemBinding =
                    ShortPeriodPickerQuarterItemBinding.inflate(inflater, parent, false)
                QuarterViewHolder(binding.root, binding, isEnabled, selection, displayedRange)
            }
            TYPE_HALF_YEAR -> {
                val binding: ShortPeriodPickerHalfYearItemBinding =
                    ShortPeriodPickerHalfYearItemBinding.inflate(inflater, parent, false)
                HalfYearViewHolder(binding.root, binding, isEnabled, selection, displayedRange)
            }
            TYPE_YEAR -> {
                val binding: ShortPeriodPickerYearItemBinding =
                    ShortPeriodPickerYearItemBinding.inflate(inflater, parent, false)
                YearViewHolder(binding.root, binding, isEnabled, selection, displayedRange)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when (holder) {
            is MonthViewHolder -> holder.bind(element as MonthItem, position, listener)
            is QuarterViewHolder -> holder.bind(element as QuarterItem, position, listener)
            is HalfYearViewHolder -> holder.bind(element as HalfYearItem, position, listener)
            is YearViewHolder -> holder.bind(element as YearItem, position, listener)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is MonthItem -> TYPE_MONTH
            is QuarterItem -> TYPE_QUARTER
            is HalfYearItem -> TYPE_HALF_YEAR
            is YearItem -> TYPE_YEAR
        }
    }

    /** Обновить данные адаптера. */
    @SuppressLint("NotifyDataSetChanged")
    internal fun updateData(newData: ArrayList<ShortPeriodPickerItem>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    /** Получить элемента по позиции. */
    internal fun getItemByPosition(position: Int): ShortPeriodPickerItem {
        return data[position]
    }

    /** Получить последнюю позицию в календаре. */
    internal fun getLastPosition() = itemCount - 1
}