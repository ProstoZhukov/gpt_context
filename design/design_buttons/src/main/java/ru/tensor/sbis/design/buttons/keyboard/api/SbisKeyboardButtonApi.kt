package ru.tensor.sbis.design.buttons.keyboard.api

import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonItemType
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardIcon

/**
 * Описание API для кнопки виртуальной клавиатуры.
 *
 * @author ra.geraskin
 */

interface SbisKeyboardButtonApi {

    /**
     * Иконка для кнопки виртуальной клавиатуры.
     */
    var keyboardIcon: SbisKeyboardIcon

    /**
     * Один из трёх типов виртуальной кнопки.
     */
    var itemType: SbisKeyboardButtonItemType

    /**
     * @SelfDocumented
     */
    var needSetupShadow: Boolean

    /**
     * Скругление кнопки.
     */
    var cornerRadiusValue: Float

}