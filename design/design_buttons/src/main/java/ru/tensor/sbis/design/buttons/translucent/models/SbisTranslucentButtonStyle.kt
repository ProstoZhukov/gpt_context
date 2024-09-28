package ru.tensor.sbis.design.buttons.translucent.models

import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle

/**
 * Стиль прозрачности для кнопок с поддержкой полупрозрачного оформления.
 *
 * @author mb.kruglova
 */
enum class SbisTranslucentButtonStyle {

    LIGHT {
        override fun getButtonStyle(): SbisButtonStyle =
            SbisButtonResourceStyle(
                buttonStyle = R.attr.sbisTranslucentButtonLightTheme,
                defaultButtonStyle = R.style.SbisTranslucentButtonDefaultLightTheme,
                roundButtonStyle = R.attr.sbisTranslucentRoundButtonLightTheme,
                defaultRoundButtonStyle = R.style.SbisTranslucentRoundButtonDefaultLightTheme
            )
    },

    DARK {
        override fun getButtonStyle(): SbisButtonStyle =
            SbisButtonResourceStyle(
                buttonStyle = R.attr.sbisTranslucentButtonDarkTheme,
                defaultButtonStyle = R.style.SbisTranslucentButtonDefaultDarkTheme,
                roundButtonStyle = R.attr.sbisTranslucentRoundButtonDarkTheme,
                defaultRoundButtonStyle = R.style.SbisTranslucentRoundButtonDefaultDarkTheme
            )
    };

    /** @SelfDocumented */
    abstract fun getButtonStyle(): SbisButtonStyle
}