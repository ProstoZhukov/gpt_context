package ru.tensor.sbis.widget_player.converter.style

import ru.tensor.sbis.widget_player.res.color.ColorRes

/**
 * @author am.boldinov
 */
data class FormattedTextAttributes(
    val formats: List<FormattedTextStyleRange> = emptyList()
) {

    companion object {

        @JvmStatic
        val EMPTY = FormattedTextAttributes()
    }
}

data class FormattedTextStyleRange(val location: Int, val length: Int, val style: FormattedTextStyle)

data class FormattedTextStyle(
    val font: FormattedTextFont? = null,
    val decoration: FormattedTextDecoration? = null,
    val color: FormattedTextColor? = null,
    val backgroundColor: FormattedTextColor? = null,
    val href: String? = null,
    val code: Boolean? = null
)

data class FormattedTextFont(
    val absoluteFontWeight: Int? = null,
    val relativeFontWeight: String? = null,
    val style: String? = null,
    val absoluteFontSize: Int? = null,
    val relativeFontSize: String? = null,
    val family: FormattedTextFontFamily? = null
)

data class FormattedTextDecoration(
    val line: ArrayList<String>? = null,
    val style: String? = null,
    val color: FormattedTextColor? = null
)

data class FormattedTextColor(
    val rgbColor: String? = null,
    val hexColor: String? = null,
    val relativeColor: String? = null,
    val absoluteColor: ColorRes? = null
)

data class FormattedTextFontFamily(
    val name: String?,
    val fallback: ArrayList<String>?
)