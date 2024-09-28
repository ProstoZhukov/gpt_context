package ru.tensor.sbis.date_picker.adapter.viewholder

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import ru.tensor.sbis.date_picker.DayNumberTextView
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.month.items.DayVM
import ru.tensor.sbis.design.sbis_text_view.BindingAdapters.setTextColorAttr

/**
 * Вьюхолдер ячейки с датой
 *
 * @author us.bessonov
 */
internal class ItemMonthDayViewHolder(private val view: DayNumberTextView) : DatePickerViewHolder(view) {

    private var callbacks: List<ObservableFieldDisposable>? = null

    /** @SelfDocumented */
    fun bind(vm: DayVM) = with(view) {
        setOnClickListener {
            vm.onClick()
        }

        dayOfMonth = vm.dayOfMonth
        dayOfWeek = vm.dayOfWeek

        callbacks = listOf(
            vm.clickable.subscribe { isClickable = vm.clickable.get() },
            vm.backgroundIdRes.subscribe { setBackgroundResource(vm.backgroundIdRes.get()) },
            vm.textColorResAttr.subscribe { setTextColorAttr(this, vm.textColorResAttr.get()) },
            vm.counter.subscribe { counter = vm.counter.get().orEmpty() }
        )
    }

    override fun recycle() {
        callbacks?.forEach { it.dispose() }
        callbacks = null
    }

    companion object {
        val ITEM_TYPE = R.id.date_picker_month_day_item_type_id

        /** @SelfDocumented */
        fun create(parent: ViewGroup) = ItemMonthDayViewHolder(
            DayNumberTextView(parent.context).apply {
                val size = resources.getDimensionPixelSize(R.dimen.date_picker_day_size)
                layoutParams = ViewGroup.LayoutParams(size, size)
                gravity = Gravity.CENTER
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.date_picker_item_period_text_size)
                )
                id = R.id.date_picker_item_day
            }
        )
    }
}

