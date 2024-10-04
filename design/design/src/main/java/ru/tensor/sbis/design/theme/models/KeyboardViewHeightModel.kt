package ru.tensor.sbis.design.theme.models

import ru.tensor.sbis.design.theme.global_variables.KeyboardViewHeight

/**
 * Модель объекта, который поставляет информацию о высоте кнопки виртуальной клавиатуры.
 *
 * @author ra.geraskin
 */
interface KeyboardViewHeightModel : AbstractHeightModel {
    override val globalVar: KeyboardViewHeight
}
