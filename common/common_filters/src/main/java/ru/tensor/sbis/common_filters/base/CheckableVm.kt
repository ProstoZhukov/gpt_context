package ru.tensor.sbis.common_filters.base

import androidx.databinding.ObservableBoolean

/**
 * Тип отметки фильтра
 */
enum class CheckStyle {
    CHECKBOX_SINGLE, // единичный выбор (простая галочка)
    CHECKBOX_MULTI, // множественный выбор (галочка с фоном)
    SWITCH, // переключатель
    NONE // ничего
}

/**
 * Интерфейс, предусматривающий возможность отметки
 *
 * @property checkStyle тип отметки
 * @property isChecked состояние отметки (активности)
 */
interface Checkable {

    val checkStyle: CheckStyle

    fun isChecked(): Boolean

    /**
     * Отмечает, либо делает неотмеченным
     */
    fun setChecked(isChecked: Boolean = true)

    /**
     * Переключает состояние активности элемента на противоположное
     */
    fun toggleCheck() {
        setChecked(!isChecked())
    }
}

/**
 * Вьюмодель элемента с возможностью отметки.
 * При клике на элемент состояние отметки изменяется на противоположное
 */
class CheckableVm(
    override val checkStyle: CheckStyle = CheckStyle.NONE,
    isChecked: Boolean = false
) : Checkable {

    val checked = ObservableBoolean(isChecked)

    override fun isChecked() = checked.get()

    override fun setChecked(isChecked: Boolean) {
        checked.set(isChecked)
    }

    fun onClick() {
        toggleCheck()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CheckableVm

        if (checkStyle != other.checkStyle) return false
        if (checked.get() != other.checked.get()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkStyle.hashCode()
        result = 31 * result + checked.get().hashCode()
        return result
    }
}
