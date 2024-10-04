package ru.tensor.sbis.design.view.input.base.utils

/**
 * Интерфейс для обновления состояния при изменении свойств вью.
 *
 * @author ps.smirnyh
 */
internal fun interface UpdateState {
    fun onChange()
}

/**
 * Интерфейс для обновления состояния при изменении фокуса.
 *
 * @author ps.smirnyh
 */
internal fun interface UpdateValueState<T> {
    fun onChange(value: T)
}