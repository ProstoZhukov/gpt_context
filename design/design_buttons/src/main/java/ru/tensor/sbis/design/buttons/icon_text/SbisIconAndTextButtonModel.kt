package ru.tensor.sbis.design.buttons.icon_text

import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle

/**
 * Модель внешнего вида кнопки с текстом и иконкой.
 *
 * @author mb.kruglova
 */
interface SbisIconAndTextButtonModel {
    val icon: SbisButtonIcon?
    val title: SbisButtonTitle?
    val state: SbisButtonState
    val style: SbisButtonStyle?
}