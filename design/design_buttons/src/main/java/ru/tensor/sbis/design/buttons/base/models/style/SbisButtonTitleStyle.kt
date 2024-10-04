package ru.tensor.sbis.design.buttons.base.models.style

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.style.providers.ResourceStyleProvider
import ru.tensor.sbis.design.buttons.base.models.style.providers.StaticStyleProvider
import ru.tensor.sbis.design.buttons.base.models.style.providers.StyleProvider
import ru.tensor.sbis.design.buttons.base.models.style.providers.ThemeStyleProvider

/**
 * Модель для раскраски текста в кнопках.
 *
 * @author ma.kolpakov
 */
data class SbisButtonTitleStyle internal constructor(
    internal val styleProvider: StyleProvider
) {

    companion object {

        val Primary = create(PrimaryButtonStyle)

        val Secondary = create(SecondaryButtonStyle)

        val Success = create(SuccessButtonStyle)

        val Unaccented = create(UnaccentedButtonStyle)

        val Bonus = create(BonusButtonStyle)

        fun create(
            @ColorRes color: Int,
            @ColorRes colorDisabled: Int,
            @ColorRes contrastColor: Int = color,
            @ColorRes transparentColor: Int = color
        ) = SbisButtonTitleStyle(
            ResourceStyleProvider(
                color,
                color,
                colorDisabled,
                contrastColor,
                contrastColor,
                colorDisabled,
                transparentColor,
                transparentColor,
                colorDisabled
            )
        )

        fun create(
            colors: ColorStateList,
            contrast: ColorStateList = colors,
            transparent: ColorStateList = colors
        ) = SbisButtonTitleStyle(StaticStyleProvider(colors, contrast, transparent))

        @Suppress("unused")
        fun createButtonStyle(
            @AttrRes textStyleAttr: Int,
            @StyleRes textStyleRes: Int
        ) = SbisButtonTitleStyle(ThemeStyleProvider(textStyleAttr, textStyleRes, R.styleable.SbisButton))

        @Suppress("unused")
        fun createRoundButtonStyle(
            @AttrRes textStyleAttr: Int,
            @StyleRes textStyleRes: Int
        ) = SbisButtonTitleStyle(ThemeStyleProvider(textStyleAttr, textStyleRes, R.styleable.SbisRoundButton))

        internal fun create(buttonStyle: SbisButtonResourceStyle) =
            SbisButtonTitleStyle(ThemeStyleProvider(buttonStyle))
    }

    internal fun loadStyle(context: Context, consumer: StyleConsumer) =
        styleProvider.loadStyle(context, consumer)
}