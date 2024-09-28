package ru.tensor.sbis.message_panel.helper

import androidx.annotation.Px
import androidx.annotation.StringRes

/**
 * Совмещённые параметры текста в панели ввода
 *
 * @author us.bessonov
 */
internal data class MessagePanelTextParams(
    val minLines: Int,
    val maxHeightParams: MaxHeightParams,
    val ellipsizeEnd: Boolean,
    val gravity: Int,
    @StringRes val hint: Int
)

/**
 * Параметры ограничения высоты текста
 *
 * @author us.bessonov
 */
internal sealed class MaxHeightParams

/**
 * Ограничение высоты поля ввода
 */
internal data class MaxHeight(@Px val height: Int) : MaxHeightParams()

/**
 * Ограничение числа строк текста
 */
internal data class MaxLines(val lines: Int) : MaxHeightParams()