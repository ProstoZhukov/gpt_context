package ru.tensor.sbis.common_filters.base

import androidx.databinding.ObservableBoolean

/**
 * Интерфейс, предусматривающий возможность сворачивания/разворачивания
 *
 * @property isExpanded действие по клику
 */
interface Expandable {

    fun isExpanded(): Boolean

    /**
     * Задаёт, должен ли элемент быть развёрнут
     */
    fun setExpanded(isExpanded: Boolean = true)

    /**
     * Изменяет состояние свёрнутости на противоположное
     */
    fun toggleExpand() {
        setExpanded(!isExpanded())
    }
}

/**
 * Вьюмодель элемента с поддержкой сворачивания/разворачивания.
 * При клике изменяет состояние свёрнутости на противоположное, если [isExpandable] истинно
 *
 * @property isExpandable должна ли поддерживаться возможность разворачивания
 */
class ExpandableVm(
    val isExpandable: Boolean = true,
    isExpanded: Boolean = false
) : Expandable {

    val expanded = ObservableBoolean(isExpanded)

    override fun isExpanded() = expanded.get()

    override fun setExpanded(isExpanded: Boolean) {
        expanded.set(isExpanded)
    }

    fun onClick() {
        toggleExpand()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpandableVm

        if (isExpandable != other.isExpandable) return false
        if (expanded.get() != other.expanded.get()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isExpandable.hashCode()
        result = 31 * result + expanded.get().hashCode()
        return result
    }
}
