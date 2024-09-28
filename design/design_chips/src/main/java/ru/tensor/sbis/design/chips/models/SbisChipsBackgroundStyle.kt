package ru.tensor.sbis.design.chips.models

import android.content.Context
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Стиль фона с возможностью выбора режима фона выбранного элемента.
 *
 * @author ps.smirnyh
 */
sealed class SbisChipsBackgroundStyle {

    /** Акцентный стиль. */
    data class Accented(internal val mode: SbisChipsStyle) : SbisChipsBackgroundStyle() {
        override fun getBackgroundColor(context: Context): Int = BackgroundColor.DEFAULT.getValue(context)

        override fun getBackgroundColorSelected(context: Context): Int = mode.getBackgroundColor(context)
    }

    /** Неакцентный стиль. */
    data class Unaccented(internal val mode: SbisChipsUnaccentedStyle) : SbisChipsBackgroundStyle() {
        override fun getBackgroundColor(context: Context): Int =
            context.getThemeColorInt(RDesign.attr.paleColor)

        override fun getBackgroundColorSelected(context: Context): Int = mode.getBackgroundColorSelected(context)
    }

    /** @SelfDocumented */
    internal abstract fun getBackgroundColor(context: Context): Int

    /** @SelfDocumented */
    internal abstract fun getBackgroundColorSelected(context: Context): Int
}