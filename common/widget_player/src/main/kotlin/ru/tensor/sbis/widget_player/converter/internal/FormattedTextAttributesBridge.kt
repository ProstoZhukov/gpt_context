package ru.tensor.sbis.widget_player.converter.internal

import ru.tensor.sbis.jsonconverter.generated.*
import ru.tensor.sbis.widget_player.converter.style.FormattedTextAttributes
import ru.tensor.sbis.widget_player.converter.style.FormattedTextColor
import ru.tensor.sbis.widget_player.converter.style.FormattedTextDecoration
import ru.tensor.sbis.widget_player.converter.style.FormattedTextFont
import ru.tensor.sbis.widget_player.converter.style.FormattedTextFontFamily
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyleRange

/**
 * @author am.boldinov
 */
internal class FormattedTextAttributesBridge(
    private val handler: FormattedTextAttributes.() -> Unit
) : SabyDocMteFormattedTextAttributesHandler() {

    override fun onFormattedTextAttributes(attributes: SabyDocMteFormattedTextAttributes): Boolean {
        handler.invoke(
            FormattedTextAttributes(
                formats = attributes.formats.map { range ->
                    FormattedTextStyleRange(
                        location = range.location,
                        length = range.length,
                        style = range.textStyle.let { style ->
                            FormattedTextStyle(
                                font = style.font?.toTextFont(),
                                decoration = style.decoration?.toTextDecoration(),
                                color = style.color?.toTextColor(),
                                backgroundColor = style.backgroundColor?.toTextColor(),
                                href = style.href,
                                code = style.code
                            )
                        }
                    )
                }
            )
        )
        return true
    }

    private fun FormatFontAttr.toTextFont(): FormattedTextFont {
        return FormattedTextFont(
            absoluteFontWeight = absoluteFontWeight,
            relativeFontWeight = relativeFontWeight,
            style = style,
            absoluteFontSize = absoluteFontSize,
            relativeFontSize = relativeFontSize,
            family = family?.let { family ->
                FormattedTextFontFamily(
                    name = family.name,
                    fallback = family.fallback
                )
            }
        )
    }

    private fun FormatDecorationAttr.toTextDecoration(): FormattedTextDecoration {
        return FormattedTextDecoration(
            line = line,
            style = style,
            color = color?.toTextColor()
        )
    }

    private fun FormatColorAttr.toTextColor(): FormattedTextColor {
        return FormattedTextColor(
            rgbColor = rgbColor,
            hexColor = hexColor,
            relativeColor = relativeColor
        )
    }
}