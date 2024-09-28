package ru.tensor.sbis.common_filters.base

import androidx.databinding.ObservableBoolean

/**
 * Интерфейс, предусматривающий возможность выбора
 *
 * @property isSelected выбран ли элемент
 */
interface Selectable {

    fun isSelected(): Boolean

    /**
     * Задаёт или отменяет выбор элемента
     */
    fun setSelected(isSelected: Boolean = true)
}

/**
 * Вьюмодель элемента с поддержкой выбора.
 * При клике элемент становится выбранным
 */
class SelectableVm(isSelected: Boolean = false) : Selectable {

    val selected = ObservableBoolean(isSelected)

    override fun isSelected() = selected.get()

    override fun setSelected(isSelected: Boolean) {
        selected.set(isSelected)
    }

    fun onClick() {
        setSelected()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectableVm

        if (selected.get() != other.selected.get()) return false

        return true
    }

    override fun hashCode(): Int {
        return selected.get().hashCode()
    }
}
