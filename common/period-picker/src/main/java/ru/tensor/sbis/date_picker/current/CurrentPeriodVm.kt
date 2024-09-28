package ru.tensor.sbis.date_picker.current

import androidx.databinding.ObservableBoolean

/**
 * @author mb.kruglova
 */
data class CurrentPeriodVm(
    val title: String,
    val description: String,
    val clickAction: (() -> Unit)? = null
) {

    val selected = ObservableBoolean()

    fun setSelected(selected: Boolean) {
        this.selected.set(selected)
    }

    fun onClick() {
        clickAction?.invoke()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrentPeriodVm

        if (selected.get() != other.selected.get()) return false

        return true
    }

    override fun hashCode(): Int {
        return selected.get().hashCode()
    }
}