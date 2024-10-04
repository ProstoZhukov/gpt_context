package ru.tensor.sbis.design.buttons.group.models

import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.SbisButtonGroup
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Размер группы кнопок [SbisButtonGroup].
 *
 * @author ma.kolpakov
 */
enum class SbisButtonGroupSize(
    internal val buttonSize: SbisButtonSize
) : AbstractHeightModel by buttonSize {

    S(SbisButtonSize.S),
    M(SbisButtonSize.M),
    L(SbisButtonSize.L)
}