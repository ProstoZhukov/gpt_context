package ru.tensor.sbis.design.chips.models

import android.content.Context
import ru.tensor.sbis.design.theme.global_variables.SelectedItemColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Стиль выбранного элемента.
 *
 * @author ps.smirnyh
 */
enum class SbisChipsStyle {

    /** @SelfDocumented */
    DEFAULT {
        override fun getBackgroundColor(context: Context): Int = SelectedItemColor.DEFAULT.getValue(context)
    },

    /** @SelfDocumented */
    PRIMARY {
        override fun getBackgroundColor(context: Context): Int = StyleColor.PRIMARY.getColor(context)
    },

    /** @SelfDocumented */
    SECONDARY {
        override fun getBackgroundColor(context: Context): Int =
            StyleColor.SECONDARY.getColor(context)
    },

    /** @SelfDocumented */
    SUCCESS {
        override fun getBackgroundColor(context: Context): Int = StyleColor.SUCCESS.getColor(context)
    },

    /** @SelfDocumented */
    WARNING {
        override fun getBackgroundColor(context: Context): Int = StyleColor.WARNING.getColor(context)
    },

    /** @SelfDocumented */
    DANGER {
        override fun getBackgroundColor(context: Context): Int = StyleColor.DANGER.getColor(context)
    },

    /** @SelfDocumented */
    INFO {
        override fun getBackgroundColor(context: Context): Int = StyleColor.INFO.getColor(context)
    };

    /** Получить цвет фона. */
    internal abstract fun getBackgroundColor(context: Context): Int

}