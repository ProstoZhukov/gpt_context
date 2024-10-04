package ru.tensor.sbis.design.chips.models

import android.content.Context
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Режим выбранного элемента неакцентного стиля фона.
 *
 * @author ps.smirnyh
 */
enum class SbisChipsUnaccentedStyle {

    /** Стандартный серый фон. */
    DEFAULT {
        override fun getBackgroundColorSelected(context: Context): Int =
            context.getThemeColorInt(R.attr.paleActiveColor)
    },

    /** Контрастный белый фон. */
    CONTRAST {
        override fun getBackgroundColorSelected(context: Context): Int = BackgroundColor.DEFAULT.getValue(context)
    };

    /** @SelfDocumented */
    internal abstract fun getBackgroundColorSelected(context: Context): Int
}