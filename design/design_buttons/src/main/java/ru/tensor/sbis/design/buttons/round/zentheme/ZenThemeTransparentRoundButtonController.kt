package ru.tensor.sbis.design.buttons.round.zentheme

import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.style.DangerButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.DefaultButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.InfoButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.LabelButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.LinkButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PaleButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.models.style.SecondaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.WarningButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.base.zentheme.plusAlpha
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации [круглых кнопок][SbisRoundButton] [без заливки][SbisRoundButtonType.Transparent].
 *
 * @author ra.geraskin
 */
internal class ZenThemeTransparentRoundButtonController : ZenThemeAbstractButtonController() {

    /** @SelfDocumented */
    override val alphaMap: Map<SbisButtonStyle, OnClickAdditionAlphaValuePair> = mapOf(
        PrimaryButtonStyle to OnClickAdditionAlphaValuePair(-.2f, +.2f),
        SecondaryButtonStyle to OnClickAdditionAlphaValuePair(-.2f, +.2f),
        DefaultButtonStyle to OnClickAdditionAlphaValuePair(-.2f, +.2f),
        LinkButtonStyle to OnClickAdditionAlphaValuePair(-.2f, +.2f),
        LabelButtonStyle to OnClickAdditionAlphaValuePair(+.2f, -.2f),
        UnaccentedButtonStyle to OnClickAdditionAlphaValuePair(+.2f, -.2f)
    )

    /** @SelfDocumented */
    override val supportedThemeStyles: List<SbisButtonResourceStyle> = listOf(
        PrimaryButtonStyle,
        SuccessButtonStyle,
        WarningButtonStyle,
        UnaccentedButtonStyle,
        PaleButtonStyle,
        LinkButtonStyle,
        SecondaryButtonStyle,
        DangerButtonStyle,
        InfoButtonStyle,
        DefaultButtonStyle,
        LabelButtonStyle
    )

    /** @SelfDocumented */
    override fun getTextColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        PrimaryButtonStyle to SbisColor(zenModel.complimentaryColor),
        SecondaryButtonStyle to zenModel.elementsColors.secondaryColor,
        DefaultButtonStyle to zenModel.elementsColors.defaultColor,
        LinkButtonStyle to zenModel.elementsColors.linkColor,
        LabelButtonStyle to zenModel.elementsColors.labelColor,
        UnaccentedButtonStyle to zenModel.elementsColors.unaccentedColor
    )

    /** @SelfDocumented */
    override fun getBorderColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /** @SelfDocumented */
    override fun getBackgroundColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /** @SelfDocumented */
    override fun createZenStyle(
        alphaAdditionalValue: Float?,
        textColor: Int?,
        backgroundColor: Int?,
        borderColor: Int?,
        disableColor: Int
    ): SbisButtonStyle? {
        if (textColor == null || alphaAdditionalValue == null) return null
        val colorsArray = intArrayOf(
            textColor.plusAlpha(alphaAdditionalValue),
            disableColor,
            textColor
        )
        val colors = ColorStateList(COLOR_STATES, colorsArray)

        val transparentColors = List(3) { Color.TRANSPARENT }.toIntArray()
        return SbisButtonCustomStyle(
            backgroundColors = ColorStateList(COLOR_STATES, transparentColors),
            contrastBackgroundColors = ColorStateList(COLOR_STATES, transparentColors),
            transparentBackgroundColors = ColorStateList(COLOR_STATES, List(3) { Color.TRANSPARENT }.toIntArray()),
            iconStyle = SbisButtonIconStyle(colors),
            titleStyle = SbisButtonTitleStyle.create(colors)
        )
    }

}