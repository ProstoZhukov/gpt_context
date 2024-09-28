package ru.tensor.sbis.common_filters.base

import androidx.databinding.ObservableBoolean

/**
 * Интерфейс, предусматривающий возможность клика
 *
 * @property onClickAction действие по клику
 */
interface Clickable {

    var onClickAction: Runnable?

    fun onClick()

    /**
     * Задаёт доступность кликов
     */
    fun setEnabled(isEnabled: Boolean = true)
}

/**
 * Вьюмодель кликабельного элемента
 */
class ClickableVm(
    override var onClickAction: Runnable? = null,
    isEnabled: Boolean = true
) : Clickable {

    val enabled = ObservableBoolean(isEnabled)

    override fun onClick() {
        onClickAction?.run()
    }

    override fun setEnabled(isEnabled: Boolean) {
        enabled.set(isEnabled)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClickableVm

        if (onClickAction != other.onClickAction) return false
        if (enabled.get() != other.enabled.get()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = onClickAction?.hashCode() ?: 0
        result = 31 * result + enabled.get().hashCode()
        return result
    }
}