package ru.tensor.sbis.design.buttons.link.zentheme

import android.content.res.ColorStateList
import ru.tensor.sbis.design.buttons.base.models.style.*
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.base.zentheme.plusAlpha
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации кнопок-ссылок.
 *
 * @author ra.geraskin
 */

internal class ZenThemeLinkButtonController : ZenThemeAbstractButtonController() {

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
        SecondaryButtonStyle,
        SuccessButtonStyle,
        DangerButtonStyle,
        WarningButtonStyle,
        InfoButtonStyle,
        DefaultButtonStyle,
        LinkButtonStyle,
        LabelButtonStyle,
        UnaccentedButtonStyle
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

        return SbisButtonCustomStyle(
            iconStyle = SbisButtonIconStyle(colors),
            titleStyle = SbisButtonTitleStyle.create(colors)
        )
    }

}
