package ru.tensor.sbis.richtext.converter.css

import android.content.Context
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.R as RDesign

/**
 * Фабрика по созданию цветов на основе поддерживаемых css классов стилизации текста.
 *
 * @author am.boldinov
 */
internal object CssColorClassFactory {
    /**
     * Создает ссылку на ресурс цвета из colors.xml на основе css класса.
     *
     * @param className название css класса
     * @return ссылка на ресурс с цветом
     */
    @ColorInt
    @JvmStatic
    fun create(context: Context, className: String): Int? {
        return when (className) {
            "colorAdditional" -> ThemeTokensProvider.getColorInt(context, RDesign.attr.paletteColor13_3)
            "colorLink2" -> TextColor.LINK.getValue(context)
            else -> null
        }
    }
}
