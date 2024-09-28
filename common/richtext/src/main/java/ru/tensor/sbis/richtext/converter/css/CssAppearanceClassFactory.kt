package ru.tensor.sbis.richtext.converter.css

import androidx.annotation.StyleRes
import ru.tensor.sbis.richtext.R

/**
 * Фабрика по созданию стилей на основе поддерживаемых css классов стилизации текста.
 *
 * @author am.boldinov
 */
internal object CssAppearanceClassFactory {
    /**
     * Создает ссылку на ресурс стиля из styles.xml на основе css класса.
     *
     * @param className название css класса
     * @return ссылка на ресурс со стилем
     */
    @StyleRes
    @JvmStatic
    fun create(className: String): Int? {
        return when (className) {
            "titleText" -> R.style.RichTextTitleStyle_H1
            "subTitleText" -> R.style.RichTextTitleStyle_H2
            "additionalText", "richEditor_Base_additionalBlock" -> R.style.RichTextCssAdditionalStyle
            "richEditor_Base_style1" -> R.style.RichTextStyle1
            "richEditor_Base_style2" -> R.style.RichTextStyle2
            "richEditor_Base_style3" -> R.style.RichTextStyle3
            "richEditor_Base_style4" -> R.style.RichTextStyle4
            "richEditor_Base_style5" -> R.style.RichTextStyle5
            "richEditor_Base_style6" -> R.style.RichTextStyle6
            "richEditor_Base_style7" -> R.style.RichTextStyle7
            "richEditor_Base_style8" -> R.style.RichTextStyle8
            else -> null
        }
    }
}

