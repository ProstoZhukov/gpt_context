package ru.tensor.sbis.design.buttons.keyboard.model

import ru.tensor.sbis.design.buttons.SbisKeyboardButton
import ru.tensor.sbis.design.theme.global_variables.KeyboardViewHeight
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Размеры кнопки [SbisKeyboardButton].
 *
 * @author ra.geraskin
 */
enum class SbisKeyboardButtonSize(
    override val globalVar: AbstractHeight,
    val iconSize: SbisKeyboardIconSize
) : AbstractHeightModel {

    S(
        globalVar = KeyboardViewHeight.S,
        iconSize = SbisKeyboardIconSize.S
    ),

    M(
        globalVar = KeyboardViewHeight.M,
        iconSize = SbisKeyboardIconSize.M
    ),

    L(
        globalVar = KeyboardViewHeight.L,
        iconSize = SbisKeyboardIconSize.L
    )

}