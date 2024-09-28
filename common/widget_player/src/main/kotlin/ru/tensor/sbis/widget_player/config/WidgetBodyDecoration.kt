package ru.tensor.sbis.widget_player.config

import ru.tensor.sbis.widget_player.converter.element.decor.TextHighlight

/**
 * Декорация элементов [ru.tensor.sbis.widget_player.converter.WidgetBody].
 *
 * @property textHighlight подсветка (выделение фоном) частей текста.
 *
 * @author am.boldinov
 */
data class WidgetBodyDecoration(
    val textHighlight: TextHighlight?
) {

    internal fun isEmpty(): Boolean {
        return textHighlight == null
    }
}

class WidgetBodyDecorationBuilder {

    var textHighlight: TextHighlight? = null

    internal fun build() = WidgetBodyDecoration(
        textHighlight = textHighlight
    )
}