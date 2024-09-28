package ru.tensor.sbis.design.buttons.button.zentheme

import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.style.PaleButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.base.zentheme.plusAlpha
import ru.tensor.sbis.design.buttons.base.zentheme.setNewAlpha
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeElementsColors
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации для обычных [кнопок][SbisButton] [с заливкой][SbisButtonBackground.Contrast].
 *
 * @author ra.geraskin
 */
internal class ZenThemeFilledButtonController : ZenThemeAbstractButtonController() {

    /** @SelfDocumented */
    override val alphaMap: Map<SbisButtonStyle, OnClickAdditionAlphaValuePair> = mapOf(
        PrimaryButtonStyle to OnClickAdditionAlphaValuePair(-.4f, -.4f),
        PaleButtonStyle to OnClickAdditionAlphaValuePair(+.4f, +.4f)
    )

    /** @SelfDocumented */
    override val supportedThemeStyles: List<SbisButtonResourceStyle> = listOf(
        PrimaryButtonStyle,
        PaleButtonStyle
    )

    /** @SelfDocumented */
    override fun getTextColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> {
        val primaryTextColor = when (zenModel.elementsColors) {
            ZenThemeElementsColors.DARK -> Color.WHITE
            ZenThemeElementsColors.LIGHT -> Color.BLACK
        }
        return mapOf(
            PrimaryButtonStyle to SbisColor(primaryTextColor),
            PaleButtonStyle to SbisColor.Icon(IconColor.DEFAULT)
        )
    }

    /** @SelfDocumented */
    override fun getBorderColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /** @SelfDocumented */
    override fun getBackgroundColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        PrimaryButtonStyle to SbisColor(zenModel.complimentaryColor),
        PaleButtonStyle to SbisColor(zenModel.dominantColor.setNewAlpha(.6f))
    )

    /** @SelfDocumented */
    override fun createZenStyle(
        alphaAdditionalValue: Float?,
        textColor: Int?,
        backgroundColor: Int?,
        borderColor: Int?,
        disableColor: Int
    ): SbisButtonStyle? {
        if (textColor == null || alphaAdditionalValue == null || backgroundColor == null) return null

        val textColorList = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                textColor,
                disableColor,
                textColor
            )
        )
        val backgroundColorList = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                backgroundColor.plusAlpha(alphaAdditionalValue),
                disableColor,
                backgroundColor
            )
        )

        return SbisButtonCustomStyle(
            backgroundColors = backgroundColorList,
            contrastBackgroundColors = backgroundColorList,
            iconStyle = SbisButtonIconStyle(textColorList),
            titleStyle = SbisButtonTitleStyle.create(textColorList)
        )
    }

}