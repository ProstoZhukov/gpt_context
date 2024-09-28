package ru.tensor.sbis.design.buttons.base.models.style

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import java.io.Serializable

/**
 * Стили кнопок.
 *
 * @author ma.kolpakov
 */
sealed class SbisButtonStyle : Serializable {

    companion object {
        /**
         * Стиль по умолчанию. Используется самомятоятельно и по частям в виде ресурсов при создании из xml.
         */
        internal val DEFAULT: SbisButtonResourceStyle
            get() = InternalDefaultStyle
    }
}

internal val InternalDefaultStyle = SbisButtonResourceStyle(
    defaultButtonStyle = R.style.SbisButtonInternalDefaultTheme,
    defaultRoundButtonStyle = R.style.SbisRoundButtonInternalDefaultTheme,
    defaultLinkButtonStyle = R.style.SbisLinkButtonInternalDefaultTheme
)

val PrimaryButtonStyle = SbisButtonResourceStyle(
    R.attr.primarySbisButtonTheme,
    R.style.SbisButtonDefaultPrimaryTheme,
    R.attr.primarySbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultPrimaryTheme,
    R.attr.primarySbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultPrimaryTheme
)

val SecondaryButtonStyle = SbisButtonResourceStyle(
    R.attr.secondarySbisButtonTheme,
    R.style.SbisButtonDefaultSecondaryTheme,
    R.attr.secondarySbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultSecondaryTheme,
    R.attr.secondarySbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultSecondaryTheme
)

val SuccessButtonStyle = SbisButtonResourceStyle(
    R.attr.successSbisButtonTheme,
    R.style.SbisButtonDefaultSuccessTheme,
    R.attr.successSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultSuccessTheme,
    R.attr.successSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultSuccessTheme
)

val UnaccentedButtonStyle = SbisButtonResourceStyle(
    R.attr.unaccentedSbisButtonTheme,
    R.style.SbisButtonDefaultUnaccentedTheme,
    R.attr.unaccentedSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultUnaccentedTheme,
    R.attr.unaccentedSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultUnaccentedTheme
)

val BonusButtonStyle = SbisButtonResourceStyle(
    R.attr.bonusSbisButtonTheme,
    R.style.SbisButtonDefaultBonusTheme,
    R.attr.bonusSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultBonusTheme,
    R.attr.bonusSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultBonusTheme
)

val DangerButtonStyle = SbisButtonResourceStyle(
    R.attr.dangerSbisButtonTheme,
    R.style.SbisButtonDefaultDangerTheme,
    R.attr.dangerSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultDangerTheme,
    R.attr.dangerSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultDangerTheme
)

val WarningButtonStyle = SbisButtonResourceStyle(
    R.attr.warningSbisButtonTheme,
    R.style.SbisButtonDefaultWarningTheme,
    R.attr.warningSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultWarningTheme,
    R.attr.warningSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultWarningTheme
)

val InfoButtonStyle = SbisButtonResourceStyle(
    R.attr.infoSbisButtonTheme,
    R.style.SbisButtonDefaultInfoTheme,
    R.attr.infoSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultInfoTheme,
    R.attr.infoSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultInfoTheme
)

val DefaultButtonStyle = SbisButtonResourceStyle(
    R.attr.defaultSbisButtonTheme,
    R.style.SbisButtonDefaultDefaultTheme,
    R.attr.defaultSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultDefaultTheme,
    R.attr.defaultSbisLinkButtonTheme,
    R.style.SbisLinkButtonDefaultDefaultTheme
)

val NavigationButtonStyle = SbisButtonResourceStyle(
    R.attr.navigationSbisButtonTheme,
    R.style.SbisButtonDefaultNavigationTheme,
    R.attr.navigationSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultNavigationTheme
)

val PaleButtonStyle = SbisButtonResourceStyle(
    R.attr.paleSbisButtonTheme,
    R.style.SbisButtonDefaultPaleTheme,
    R.attr.paleSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultPaleTheme
)

val BrandButtonStyle = SbisButtonResourceStyle(
    R.attr.brandSbisButtonTheme,
    R.style.SbisButtonDefaultBrandTheme,
    R.attr.brandSbisRoundButtonTheme,
    R.style.SbisRoundButtonDefaultBrandTheme
)

val LinkButtonStyle = SbisButtonResourceStyle(
    roundButtonStyle = R.attr.linkSbisRoundButtonTheme,
    defaultRoundButtonStyle = R.style.SbisRoundButtonDefaultLinkTheme,
    linkButtonStyle = R.attr.linkSbisLinkButtonTheme,
    defaultLinkButtonStyle = R.style.SbisLinkButtonDefaultLinkTheme
)

val LabelButtonStyle = SbisButtonResourceStyle(
    roundButtonStyle = R.attr.labelSbisRoundButtonTheme,
    defaultRoundButtonStyle = R.style.SbisRoundButtonDefaultLabelTheme,
    linkButtonStyle = R.attr.labelSbisLinkButtonTheme,
    defaultLinkButtonStyle = R.style.SbisLinkButtonDefaultLabelTheme
)

/**
 * Стиль с загрузкой аттрибутов из XML.
 *
 * @author ma.kolpakov
 */
data class SbisButtonResourceStyle(
    @AttrRes val buttonStyle: Int = 0,
    @StyleRes val defaultButtonStyle: Int = 0,
    @AttrRes val roundButtonStyle: Int = 0,
    @StyleRes val defaultRoundButtonStyle: Int = 0,
    @AttrRes val linkButtonStyle: Int = 0,
    @StyleRes val defaultLinkButtonStyle: Int = 0
) : SbisButtonStyle()

/**
 * Кастомный стиль для использования произвольных значений аттрибутов, задаваемый программно.
 *
 * @author ma.kolpakov
 */
data class SbisButtonCustomStyle(
    var backgroundColors: ColorStateList = ColorStateList.valueOf(Color.MAGENTA),
    var contrastBackgroundColors: ColorStateList = backgroundColors,
    var transparentBackgroundColors: ColorStateList = backgroundColors,
    var gradientBackgroundColors: ColorStateList = contrastBackgroundColors,

    var borderColors: ColorStateList = backgroundColors,
    @Px
    var borderWidth: Int = 0,

    var titleStyle: SbisButtonTitleStyle? = null,

    var iconStyle: SbisButtonIconStyle? = null,

    @ColorInt
    var progressColor: Int = 0,
    @ColorInt
    var progressContrastColor: Int = 0
) : SbisButtonStyle() {

    /**
     * Кастомный стиль для использования произвольных значений аттрибутов, задаваемый программно.
     *
     * Задавать цвета следует в определенном порядке, в соответствии со состояними:
     * Pressed, Disabled, Default.
     */
    constructor(
        backgroundColors: IntArray,
        contrastBackgroundColors: IntArray = backgroundColors,
        transparentBackgroundColors: IntArray = backgroundColors,
        gradientBackgroundColors: IntArray = contrastBackgroundColors,

        borderColors: IntArray = backgroundColors,
        @Px
        borderWidth: Int = 0,

        titleStyle: SbisButtonTitleStyle? = null,

        iconStyle: SbisButtonIconStyle? = null,

        @ColorInt
        progressColor: Int = 0,
        @ColorInt
        progressContrastColor: Int = 0

    ) : this(
        backgroundColors = ColorStateList(COLOR_STATES, backgroundColors),
        contrastBackgroundColors = ColorStateList(COLOR_STATES, contrastBackgroundColors),
        transparentBackgroundColors = ColorStateList(COLOR_STATES, transparentBackgroundColors),
        gradientBackgroundColors = ColorStateList(COLOR_STATES, gradientBackgroundColors),
        borderColors = ColorStateList(COLOR_STATES, borderColors),
        borderWidth = borderWidth,
        titleStyle = titleStyle,
        iconStyle = iconStyle,
        progressColor = progressColor,
        progressContrastColor = progressContrastColor
    )

    constructor(
        backgroundColor: Int,
        contrastBackgroundColor: Int = backgroundColor,
        transparentBackgroundColor: Int = backgroundColor,
        gradientBackgroundColor: Int = contrastBackgroundColor,

        borderColor: Int = backgroundColor,
        @Px
        borderWidth: Int = 0,

        titleStyle: SbisButtonTitleStyle? = null,

        iconStyle: SbisButtonIconStyle? = null,

        @ColorInt
        progressColor: Int = 0,
        @ColorInt
        progressContrastColor: Int = 0
    ) : this(
        backgroundColors = ColorStateList.valueOf(backgroundColor),
        contrastBackgroundColors = ColorStateList.valueOf(contrastBackgroundColor),
        transparentBackgroundColors = ColorStateList.valueOf(transparentBackgroundColor),
        gradientBackgroundColors = ColorStateList.valueOf(gradientBackgroundColor),
        borderColors = ColorStateList.valueOf(borderColor),
        borderWidth = borderWidth,
        titleStyle = titleStyle,
        iconStyle = iconStyle,
        progressColor = progressColor,
        progressContrastColor = progressContrastColor
    )

    var titleColors = backgroundColors
    var titleContrastColors = titleColors
    var titleTransparentColors = titleColors

    var iconColors = titleColors
    var iconContrastColors = titleColors
    var iconTransparentColors = titleColors
}
