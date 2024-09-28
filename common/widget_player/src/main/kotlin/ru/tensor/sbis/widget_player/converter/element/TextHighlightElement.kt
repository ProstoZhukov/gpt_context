package ru.tensor.sbis.widget_player.converter.element

import ru.tensor.sbis.widget_player.converter.element.decor.TextHighlight

/**
 * Элемент с поддержкой подсветки текста.
 * Например может использоваться в элементах, текст которых должен выделяться при поиске.
 *
 * @author am.boldinov
 */
interface TextHighlightElement {

    var textHighlight: TextHighlight?
}