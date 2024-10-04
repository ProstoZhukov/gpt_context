package ru.tensor.sbis.design.buttons.round.model

import androidx.annotation.DimenRes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Размеры кнопки [SbisRoundButton].
 *
 * @author ma.kolpakov
 */
enum class SbisRoundButtonSize(
    override val globalVar: AbstractHeight,
    internal val iconSize: SbisButtonIconSize,
    @DimenRes internal val progressSize: Int,
    @DimenRes internal val progressWidth: Int
) : AbstractHeightModel {

    XS(
        globalVar = InlineHeight.X3S,
        iconSize = SbisButtonIconSize.S,
        progressSize = R.dimen.design_buttons_button_s_progress_size,
        progressWidth = R.dimen.design_buttons_button_s_progress_width
    ),
    S(
        globalVar = InlineHeight.X2S,
        iconSize = SbisButtonIconSize.XL,
        progressSize = R.dimen.design_buttons_button_s_progress_size,
        progressWidth = R.dimen.design_buttons_button_s_progress_width
    ),
    M(
        globalVar = InlineHeight.M,
        iconSize = SbisButtonIconSize.X2L,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    ),
    L(
        globalVar = InlineHeight.XL,
        iconSize = SbisButtonIconSize.X4L,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    ),
    XL(
        globalVar = InlineHeight.X3L,
        iconSize = SbisButtonIconSize.X5L,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    )
}
