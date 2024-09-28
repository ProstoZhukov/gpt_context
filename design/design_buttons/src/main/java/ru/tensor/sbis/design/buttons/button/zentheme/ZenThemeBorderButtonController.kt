package ru.tensor.sbis.design.buttons.button.zentheme

import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.buttons.SbisButton
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
import ru.tensor.sbis.design.buttons.base.zentheme.setNewAlpha
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации для обычных [кнопок][SbisButton] с [обводкой][SbisButtonBackground.Default].
 *
 * @author ra.geraskin
 */
internal class ZenThemeBorderButtonController(private val borderWidth: Int) : ZenThemeAbstractButtonController() {

    /** @SelfDocumented */
    override val alphaMap: Map<SbisButtonStyle, OnClickAdditionAlphaValuePair> = mapOf(
        PrimaryButtonStyle to OnClickAdditionAlphaValuePair(+.5f, +.5f),
        SuccessButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        WarningButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        UnaccentedButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        PaleButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        LinkButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        SecondaryButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        DangerButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        InfoButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        DefaultButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f),
        LabelButtonStyle to OnClickAdditionAlphaValuePair(+.2f, +.2f)
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
        PrimaryButtonStyle to zenModel.elementsColors.secondaryColor,
        SuccessButtonStyle to zenModel.elementsColors.secondaryColor,
        WarningButtonStyle to zenModel.elementsColors.secondaryColor,
        UnaccentedButtonStyle to zenModel.elementsColors.secondaryColor,
        PaleButtonStyle to zenModel.elementsColors.secondaryColor,
        LinkButtonStyle to zenModel.elementsColors.secondaryColor,
        SecondaryButtonStyle to zenModel.elementsColors.secondaryColor,
        DangerButtonStyle to zenModel.elementsColors.secondaryColor,
        InfoButtonStyle to zenModel.elementsColors.secondaryColor,
        DefaultButtonStyle to zenModel.elementsColors.secondaryColor,
        LabelButtonStyle to zenModel.elementsColors.secondaryColor
    )

    /** @SelfDocumented */
    override fun getBorderColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        PrimaryButtonStyle to SbisColor(zenModel.complimentaryColor),
        SuccessButtonStyle to zenModel.elementsColors.secondaryColor,
        WarningButtonStyle to zenModel.elementsColors.secondaryColor,
        UnaccentedButtonStyle to SbisColor(Color.TRANSPARENT),
        PaleButtonStyle to zenModel.elementsColors.secondaryColor,
        LinkButtonStyle to zenModel.elementsColors.secondaryColor,
        SecondaryButtonStyle to zenModel.elementsColors.secondaryColor,
        DangerButtonStyle to zenModel.elementsColors.secondaryColor,
        InfoButtonStyle to zenModel.elementsColors.secondaryColor,
        DefaultButtonStyle to zenModel.elementsColors.secondaryColor,
        LabelButtonStyle to zenModel.elementsColors.secondaryColor
    )

    /** @SelfDocumented */
    override fun getBackgroundColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = mapOf(
        PrimaryButtonStyle to SbisColor(zenModel.complimentaryColor),
        SuccessButtonStyle to zenModel.elementsColors.secondaryColor,
        WarningButtonStyle to zenModel.elementsColors.secondaryColor,
        UnaccentedButtonStyle to zenModel.elementsColors.secondaryColor,
        PaleButtonStyle to zenModel.elementsColors.secondaryColor,
        LinkButtonStyle to zenModel.elementsColors.secondaryColor,
        SecondaryButtonStyle to zenModel.elementsColors.secondaryColor,
        DangerButtonStyle to zenModel.elementsColors.secondaryColor,
        InfoButtonStyle to zenModel.elementsColors.secondaryColor,
        DefaultButtonStyle to zenModel.elementsColors.secondaryColor,
        LabelButtonStyle to zenModel.elementsColors.secondaryColor
    )

    /** @SelfDocumented */
    override fun createZenStyle(
        alphaAdditionalValue: Float?,
        textColor: Int?,
        backgroundColor: Int?,
        borderColor: Int?,
        disableColor: Int
    ): SbisButtonStyle? {
        if (textColor == null || alphaAdditionalValue == null || borderColor == null || backgroundColor == null) {
            return null
        }

        val textColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                textColor,
                disableColor,
                textColor
            )
        )

        val transparentBackgroundColor = backgroundColor.setNewAlpha(0f)
        val backgroundColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                transparentBackgroundColor.plusAlpha(alphaAdditionalValue),
                disableColor,
                transparentBackgroundColor
            )
        )

        val borderColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                borderColor,
                disableColor,
                borderColor
            )
        )

        return SbisButtonCustomStyle(
            backgroundColors = backgroundColors,
            borderColors = borderColors,
            borderWidth = borderWidth,
            iconStyle = SbisButtonIconStyle(textColors),
            titleStyle = SbisButtonTitleStyle.create(textColors)
        )
    }

}