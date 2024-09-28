package ru.tensor.sbis.date_picker.year.items

import androidx.databinding.ObservableInt
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.items.CalendarGridItemVM

/**
 * Ячейка месяца
 * @param monthShort короткий выводимый текст месяца
 * @param isCurrent true если месяц текущий, иначе false
 * @param hasFixedIndicator true если нужно добавить отступ перед [monthShort] для центрирования, иначе false
 *
 * @author mb.kruglova
 */
data class MonthVM(
    val monthShort: String,
    private val isCurrent: Boolean,
    val hasFixedIndicator: Boolean
) : CalendarGridItemVM(R.drawable.date_picker_item_default_background_with_border) {

    /** Идентификатор аттрибута цвета текста перед галочкой */
    var checkBoxColorAttr = ObservableInt(R.attr.date_picker_default_checkbox_color)
        private set

    init {
        if (isCurrent) {
            setCurrentDateAppearance()
        }
    }

    /** Действие по клику */
    var clickAction: (() -> Unit)? = null

    /** Вызывает действие по клику */
    fun onClick() {
        clickAction?.invoke()
    }

    /** Меняет цвет галочки */
    fun setFixed() {
        checkBoxColorAttr.set(R.attr.date_picker_fixed_month_color)
    }

    override fun merge(other: CalendarGridItemVM) {
        super.merge(other)
        if (other !is MonthVM) return
        checkBoxColorAttr = other.checkBoxColorAttr
    }
}
