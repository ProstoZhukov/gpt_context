package ru.tensor.sbis.design.buttons.group.api

import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisButtonGroup
import ru.tensor.sbis.design.buttons.group.models.SbisButtonGroupSize
import ru.tensor.sbis.design.utils.theme.AbstractHeightCompatibilityView

/**
 * Описание API для управления группой кнопок [SbisButtonGroup].
 *
 * @author ma.kolpakov
 */
interface SbisButtonGroupApi : AbstractHeightCompatibilityView<SbisButtonGroupSize> {

    var state: SbisButtonState

    var size: SbisButtonGroupSize

    var buttons: List<SbisButton>
}
