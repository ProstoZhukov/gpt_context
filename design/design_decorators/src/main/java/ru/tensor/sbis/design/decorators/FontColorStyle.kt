package ru.tensor.sbis.design.decorators

import android.content.Context
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Стиль текста.
 *
 * @author ps.smirnyh
 */
sealed interface FontColorStyle {

    /** Стандартные стили теста. */
    enum class Defaults : FontColorStyle {
        PRIMARY {
            override fun getColor(context: Context) = StyleColor.PRIMARY.getTextColor(context)
        },
        SECONDARY {
            override fun getColor(context: Context) = StyleColor.SECONDARY.getTextColor(context)
        },
        SUCCESS {
            override fun getColor(context: Context) = StyleColor.SUCCESS.getTextColor(context)
        },
        DANGER {
            override fun getColor(context: Context) = StyleColor.DANGER.getTextColor(context)
        },
        WARNING {
            override fun getColor(context: Context) = StyleColor.WARNING.getTextColor(context)
        },
        UNACCENTED {
            override fun getColor(context: Context) = StyleColor.UNACCENTED.getTextColor(context)
        },
        LINK {
            override fun getColor(context: Context) = TextColor.LINK.getValue(context)
        },
        LABEL {
            override fun getColor(context: Context) = TextColor.LABEL.getValue(context)
        },
        DEFAULT {
            override fun getColor(context: Context) = TextColor.DEFAULT.getValue(context)
        },
        CONTRAST {
            override fun getColor(context: Context) = TextColor.CONTRAST.getValue(context)
        },
        READ_ONLY {
            override fun getColor(context: Context) = TextColor.READ_ONLY.getValue(context)
        },
        BRAND {
            override fun getColor(context: Context) = OtherColor.BRAND.getValue(context)
        }
    }

    /** Кастомный стиль, если цвет не входит в стандартную палитру. */
    class Custom(private val color: SbisColor) : FontColorStyle {
        override fun getColor(context: Context): Int {
            return color.getColor(context)
        }
    }

    /** @SelfDocumented */
    fun getColor(context: Context): Int
}
