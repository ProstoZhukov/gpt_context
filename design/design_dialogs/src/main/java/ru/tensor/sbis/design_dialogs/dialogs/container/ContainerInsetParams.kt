package ru.tensor.sbis.design_dialogs.dialogs.container

import kotlin.math.roundToInt

/**
 * Параметры инсета контейнера
 *
 * @property fullSize   Максимальный размер
 * @property actualSize Текущий размер
 */
data class ContainerInsetParams(val fullSize: Float, val actualSize: Float) {
    override fun toString(): String = "Inset(full - ${fullSize.roundToInt()}, actual - ${actualSize.roundToInt()})"
}
