package ru.tensor.sbis.date_picker.year.items

import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * Ячейка квартала
 *
 * @author mb.kruglova
 */
data class QuarterVM(override val label: String) : NamedItemVM(label) {

    var clickAction: (() -> Unit)? = null

    fun onClick() {
        clickAction?.invoke()
    }
}