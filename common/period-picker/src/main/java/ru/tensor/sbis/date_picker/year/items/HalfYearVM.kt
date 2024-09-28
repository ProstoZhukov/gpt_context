package ru.tensor.sbis.date_picker.year.items

import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * Ячейка полугодия
 *
 * @author mb.kruglova
 */
data class HalfYearVM(override val label: String) : NamedItemVM(label) {

    var clickAction: ((HalfYearVM) -> Unit)? = null

    fun onClick() {
        clickAction?.invoke(this)
    }
}