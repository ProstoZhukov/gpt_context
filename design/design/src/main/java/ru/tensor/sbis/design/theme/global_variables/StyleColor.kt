package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.StyleColorModel

/**
 * Линейка смысловых цветов из глобальных переменных.
 *
 * Реализует [StyleColorModel].
 *
 * @author mb.kruglova
 */
enum class StyleColor(
    @AttrRes private val colorAttrRes: Int,
    @AttrRes private val textColorAttrRes: Int,
    @AttrRes private val iconColorAttrRes: Int,
    @AttrRes private val borderColorAttrRes: Int,
    @AttrRes private val activeColorAttrRes: Int,
    @AttrRes private val backgroundColorAttrRes: Int,
    @AttrRes private val activeBackgroundColorAttrRes: Int,
    @AttrRes private val sameBackgroundColorAttrRes: Int,
    @AttrRes private val activeSameBackgroundColorAttrRes: Int,
    @AttrRes private val adaptiveBackgroundColorAttrRes: Int
) : StyleColorModel {

    PRIMARY(
        colorAttrRes = R.attr.primaryColor,
        textColorAttrRes = R.attr.primaryTextColor,
        iconColorAttrRes = R.attr.primaryIconColor,
        borderColorAttrRes = R.attr.primaryBorderColor,
        activeColorAttrRes = R.attr.primaryActiveColor,
        backgroundColorAttrRes = R.attr.primaryBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.primaryActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.primarySameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.primaryActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    SECONDARY(
        colorAttrRes = R.attr.secondaryColor,
        textColorAttrRes = R.attr.secondaryTextColor,
        iconColorAttrRes = R.attr.secondaryIconColor,
        borderColorAttrRes = R.attr.secondaryBorderColor,
        activeColorAttrRes = R.attr.secondaryActiveColor,
        backgroundColorAttrRes = R.attr.secondaryBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.secondaryActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.secondarySameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.secondaryActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    DANGER(
        colorAttrRes = R.attr.dangerColor,
        textColorAttrRes = R.attr.dangerTextColor,
        iconColorAttrRes = R.attr.dangerIconColor,
        borderColorAttrRes = R.attr.dangerBorderColor,
        activeColorAttrRes = R.attr.dangerActiveColor,
        backgroundColorAttrRes = R.attr.dangerBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.dangerActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.dangerSameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.dangerActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    SUCCESS(
        colorAttrRes = R.attr.successColor,
        textColorAttrRes = R.attr.successTextColor,
        iconColorAttrRes = R.attr.successIconColor,
        borderColorAttrRes = R.attr.successBorderColor,
        activeColorAttrRes = R.attr.successActiveColor,
        backgroundColorAttrRes = R.attr.successBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.successActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.successSameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.successActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    WARNING(
        colorAttrRes = R.attr.warningColor,
        textColorAttrRes = R.attr.warningTextColor,
        iconColorAttrRes = R.attr.warningIconColor,
        borderColorAttrRes = R.attr.warningBorderColor,
        activeColorAttrRes = R.attr.warningActiveColor,
        backgroundColorAttrRes = R.attr.warningBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.warningActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.warningSameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.warningActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    INFO(
        colorAttrRes = R.attr.infoColor,
        textColorAttrRes = R.attr.infoTextColor,
        iconColorAttrRes = R.attr.infoIconColor,
        borderColorAttrRes = R.attr.infoBorderColor,
        activeColorAttrRes = R.attr.infoActiveColor,
        backgroundColorAttrRes = R.attr.infoBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.infoActiveBackgroundColor,
        sameBackgroundColorAttrRes = R.attr.infoSameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.infoActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    UNACCENTED(
        colorAttrRes = R.attr.unaccentedContrastBackgroundColor,
        textColorAttrRes = R.attr.unaccentedTextColor,
        iconColorAttrRes = R.attr.unaccentedIconColor,
        borderColorAttrRes = R.attr.unaccentedBorderColor,
        activeColorAttrRes = 0,
        backgroundColorAttrRes = R.attr.unaccentedBackgroundColor,
        activeBackgroundColorAttrRes = R.attr.unaccentedActiveBackgroundColor,
        sameBackgroundColorAttrRes = 0,
        activeSameBackgroundColorAttrRes = 0,
        adaptiveBackgroundColorAttrRes = R.attr.unaccentedAdaptiveBackgroundColor
    ),
    BONUS(
        colorAttrRes = R.attr.bonusContrastBackgroundColor,
        textColorAttrRes = R.attr.bonusTextColor,
        iconColorAttrRes = R.attr.bonusIconColor,
        borderColorAttrRes = R.attr.bonusBorderColor,
        activeColorAttrRes = R.attr.bonusActiveSameBackgroundColor,
        backgroundColorAttrRes = 0,
        activeBackgroundColorAttrRes = 0,
        sameBackgroundColorAttrRes = R.attr.bonusSameBackgroundColor,
        activeSameBackgroundColorAttrRes = R.attr.bonusActiveSameBackgroundColor,
        adaptiveBackgroundColorAttrRes = 0
    ),
    PALE(
        colorAttrRes = R.attr.paleColor,
        textColorAttrRes = R.attr.paleTextColor,
        iconColorAttrRes = R.attr.paleIconColor,
        borderColorAttrRes = R.attr.paleBorderColor,
        activeColorAttrRes = R.attr.paleActiveColor,
        backgroundColorAttrRes = R.attr.paleBackgroundColor,
        activeBackgroundColorAttrRes = 0,
        sameBackgroundColorAttrRes = 0,
        activeSameBackgroundColorAttrRes = 0,
        adaptiveBackgroundColorAttrRes = 0
    );

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getTextColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, textColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getIconColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, iconColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getBorderColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, borderColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getBackgroundColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, backgroundColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getActiveBackgroundColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, activeBackgroundColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getSameBackgroundColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, sameBackgroundColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getActiveSameBackgroundColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, activeSameBackgroundColorAttrRes)

    /**
     * TODO: Будет удалён по задаче: https://dev.sbis.ru/doc/6e5d7352-ddab-4dbe-a138-ccfc6f64379e?client=3
     */
    @Deprecated("Используй другой метод.", ReplaceWith("getColor(context)"))
    @ColorInt
    fun getContrastBackgroundColor(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getColor(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getActiveColor(context: Context) = ThemeTokensProvider.getColorInt(context, activeColorAttrRes)

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getAdaptiveBackgroundColor(context: Context) =
        ThemeTokensProvider.getColorInt(context, adaptiveBackgroundColorAttrRes)
}