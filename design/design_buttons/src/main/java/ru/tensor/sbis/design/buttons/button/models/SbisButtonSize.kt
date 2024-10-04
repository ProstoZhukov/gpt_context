package ru.tensor.sbis.design.buttons.button.models

import android.content.Context
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Размеры кнопки [SbisButton].
 *
 * @author ma.kolpakov
 */
enum class SbisButtonSize(
    override val globalVar: AbstractHeight,
    internal val iconSize: SbisButtonIconSize,
    internal val titleSize: SbisButtonTitleSize,
    internal val innerSpacing: Offset,
    @DimenRes internal val progressSize: Int,
    @DimenRes internal val progressWidth: Int
) : AbstractHeightModel {

    XS(
        globalVar = InlineHeight.X3S,
        iconSize = SbisButtonIconSize.S,
        titleSize = SbisButtonTitleSize.M,
        innerSpacing = Offset.XS,
        progressSize = R.dimen.design_buttons_button_s_progress_size,
        progressWidth = R.dimen.design_buttons_button_s_progress_width
    ),
    S(
        globalVar = InlineHeight.X2S,
        iconSize = SbisButtonIconSize.XL,
        titleSize = SbisButtonTitleSize.M,
        innerSpacing = Offset.XS,
        progressSize = R.dimen.design_buttons_button_s_progress_size,
        progressWidth = R.dimen.design_buttons_button_s_progress_width
    ),
    M(
        globalVar = InlineHeight.M,
        iconSize = SbisButtonIconSize.X2L,
        titleSize = SbisButtonTitleSize.X2L,
        innerSpacing = Offset.XS,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    ),
    L(
        globalVar = InlineHeight.XL,
        iconSize = SbisButtonIconSize.X4L,
        titleSize = SbisButtonTitleSize.X3L,
        innerSpacing = Offset.XS,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    ),
    XL(
        globalVar = InlineHeight.X3L,
        iconSize = SbisButtonIconSize.X5L,
        titleSize = SbisButtonTitleSize.X3L,
        innerSpacing = Offset.XS,
        progressSize = R.dimen.design_buttons_button_m_progress_size,
        progressWidth = R.dimen.design_buttons_button_m_progress_width
    );

    /**
     * @see Offset.getDimen
     */
    @Dimension
    fun getInnerSpacingDimen(context: Context) = innerSpacing.getDimen(context)

    /**
     * Получить радиус скругления.
     * По умолчанию радиус скругления вычисляется как половина inlineSize.
     */
    @Dimension
    fun getCornerRadiusDimen(context: Context) = globalVar.getDimen(context) / 2F
}
