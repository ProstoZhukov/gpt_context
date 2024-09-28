package ru.tensor.sbis.widget_player.widget.text

import android.text.Layout.Alignment
import ru.tensor.sbis.widget_player.converter.style.FormattedTextAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.element.TextElement
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.decor.TextHighlight
import ru.tensor.sbis.widget_player.converter.style.TextAlignment

/**
 * @author am.boldinov
 */
class FormattedTextElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources
) : WidgetElement(tag, attributes, resources), TextElement {

    override var textAttributes: FormattedTextAttributes = FormattedTextAttributes.EMPTY

    override var text: String = ""

    override var textHighlight: TextHighlight? = null

    val textAlign get() = style.textAlignment.toLayoutAlign()

    private fun TextAlignment.toLayoutAlign(): Alignment {
        return when (this) {
            TextAlignment.LEFT   -> Alignment.ALIGN_NORMAL
            TextAlignment.CENTER -> Alignment.ALIGN_CENTER
            TextAlignment.RIGHT  -> Alignment.ALIGN_OPPOSITE
        }
    }
}