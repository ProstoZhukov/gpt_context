package ru.tensor.sbis.design.buttons.base.api

import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Описание базового API для управления кнопкой.
 *
 * @author ma.kolpakov
 */
interface AbstractButtonApi<SIZE : AbstractHeightModel> {

    /** @SelfDocumented */
    var state: SbisButtonState

    /** @SelfDocumented */
    var style: SbisButtonStyle

    /** @SelfDocumented */
    var size: SIZE

    /** Переключатель скалируемости и заголовка кнопки. */
    var scaleOn: Boolean
}
