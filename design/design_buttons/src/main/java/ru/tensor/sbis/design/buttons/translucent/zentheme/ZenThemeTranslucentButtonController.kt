package ru.tensor.sbis.design.buttons.translucent.zentheme

import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.zentheme.ZenThemeAbstractButtonController
import ru.tensor.sbis.design.buttons.base.zentheme.plusAlpha
import ru.tensor.sbis.design.buttons.base.zentheme.setNewAlpha
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.translucent.models.SbisTranslucentButtonStyle.DARK
import ru.tensor.sbis.design.buttons.translucent.models.SbisTranslucentButtonStyle.LIGHT
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeElementsColors
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации прозрачных кнопок (подходит и для [круглых кнопок][SbisRoundButton] и для
 * [обычных кнопок][SbisButton]).
 *
 * @author ra.geraskin
 */
internal class ZenThemeTranslucentButtonController : ZenThemeAbstractButtonController() {

    /** @SelfDocumented */
    override val alphaMap: Map<SbisButtonStyle, OnClickAdditionAlphaValuePair> by lazy {
        mapOf(
            DARK.getButtonStyle() to OnClickAdditionAlphaValuePair(-.2f, -.2f),
            LIGHT.getButtonStyle() to OnClickAdditionAlphaValuePair(-.2f, -.2f)
        )
    }

    /** @SelfDocumented */
    override val supportedThemeStyles: List<SbisButtonStyle> by lazy {
        listOf(
            DARK.getButtonStyle(),
            LIGHT.getButtonStyle()
        )
    }

    /** @SelfDocumented */
    override fun getBackgroundColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        DARK.getButtonStyle() to SbisColor(zenModel.elementsColors.getBackgroundColorByZenTheme()),
        LIGHT.getButtonStyle() to SbisColor(zenModel.elementsColors.getBackgroundColorByZenTheme())
    )

    /** @SelfDocumented */
    override fun getTextColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        DARK.getButtonStyle() to SbisColor(zenModel.elementsColors.getTextIconColorByZenTheme()),
        LIGHT.getButtonStyle() to SbisColor(zenModel.elementsColors.getTextIconColorByZenTheme())
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
                Color.TRANSPARENT,
                backgroundColor
            )
        )

        val borderColorStateList = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                Color.TRANSPARENT,
                disableColor,
                Color.TRANSPARENT
            )
        )

        return SbisButtonCustomStyle(
            backgroundColors = backgroundColorList,
            contrastBackgroundColors = backgroundColorList,
            borderColors = borderColorStateList,
            iconStyle = SbisButtonIconStyle(textColorList),
            titleStyle = SbisButtonTitleStyle.create(textColorList)
        )
    }

    /** @SelfDocumented */
    private fun ZenThemeElementsColors.getBackgroundColorByZenTheme() = when (this) {
        ZenThemeElementsColors.LIGHT -> Color.WHITE.setNewAlpha(.5f)
        ZenThemeElementsColors.DARK -> Color.BLACK.setNewAlpha(.7f)
    }

    /** @SelfDocumented */
    private fun ZenThemeElementsColors.getTextIconColorByZenTheme() = when (this) {
        ZenThemeElementsColors.LIGHT -> Color.BLACK
        ZenThemeElementsColors.DARK -> Color.WHITE
    }

}