package ru.tensor.sbis.folderspanel

import androidx.databinding.ObservableBoolean

/**
 * Базовый класс для элементов списка с функцией выделения
 */
@Suppress("unused")
abstract class SelectableItem {

    /**@SelfDocumented*/
    val selected = ObservableBoolean()

    /**
     * Выбрать эелемент
     */
    fun setSelected(selected: Boolean) {
        this.selected.set(selected)
    }

    /**@SelfDocumented*/
    abstract fun getItemUuid(): String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectableItem

        if (selected.get() != other.selected.get()) return false

        return true
    }

    override fun hashCode(): Int {
        return selected.get().hashCode()
    }
}