package ru.tensor.sbis.date_picker.adapter.viewholder

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.month.items.MonthLabelVM
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.view_ext.SimplifiedTextView

/**
 * Вьюхолдер элемента с указанием месяца
 *
 * @author us.bessonov
 */
internal class ItemMonthLabelViewHolder(private val view: SimplifiedTextView) : DatePickerViewHolder(view) {

    private var callback: ObservableFieldDisposable? = null

    /** @SelfDocumented */
    fun bind(vm: MonthLabelVM) = with(view) {
        setOnClickListener {
            vm.onMonthClick()
        }
        text = vm.label
        callback = vm.fontStyle.subscribe { setFontStyle(vm.fontStyle.get()) }
    }

    override fun recycle() {
        callback?.dispose()
        callback = null
    }

    companion object {
        val ITEM_TYPE = R.id.date_picker_month_label_item_type_id

        /** @SelfDocumented */
        fun create(parent: ViewGroup) = ItemMonthLabelViewHolder(
            SimplifiedTextView(parent.context).apply {
                val height = resources.getDimensionPixelSize(R.dimen.date_picker_day_size)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                gravity = Gravity.CENTER
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(ru.tensor.sbis.design.R.dimen.size_title2_scaleOff)
                )
                context.getDataFromAttrOrNull(R.attr.date_picker_item_title_background)
                    ?.let(::setBackgroundColor)
                context.getDataFromAttrOrNull(R.attr.date_picker_current_month_color)
                    ?.let(::setTextColor)
                id = R.id.date_picker_item_month
            }
        )

    }
}