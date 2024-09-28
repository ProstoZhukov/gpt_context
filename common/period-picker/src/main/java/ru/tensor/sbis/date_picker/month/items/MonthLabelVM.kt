package ru.tensor.sbis.date_picker.month.items

import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * @author mb.kruglova
 */
data class MonthLabelVM(
    override val label: String,
    private val monthClicked: ((NamedItemVM) -> Unit)?
) : NamedItemVM(label) {

    fun onMonthClick() {
        monthClicked?.invoke(this)
    }
}