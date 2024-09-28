package ru.tensor.sbis.design.buttons.keyboard.model

import android.annotation.SuppressLint
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize

/**
 * Размер иконки кнопки виртуальной клавиатуры.
 * Разделение на отдельные размеры для текстовых и шрифтовых иконок обусловлено различными размерами соответствующих
 * типов иконок при одном и том же размере кнопки.
 *
 * @author ra.geraskin
 */
enum class SbisKeyboardIconSize(
    val textIconSize: SbisButtonTitleSize,
    val mobileIconSize: SbisButtonIconSize
) {

    @SuppressLint("DiscouragedApi")
    S(
        textIconSize = SbisButtonTitleSize.X5L,
        mobileIconSize = SbisButtonIconSize.X7L
    ),

    @SuppressLint("DiscouragedApi")
    M(
        textIconSize = SbisButtonTitleSize.X5L,
        mobileIconSize = SbisButtonIconSize.X7L
    ),

    @SuppressLint("DiscouragedApi")
    L(
        textIconSize = SbisButtonTitleSize.X5L,
        mobileIconSize = SbisButtonIconSize.X7L
    )
}