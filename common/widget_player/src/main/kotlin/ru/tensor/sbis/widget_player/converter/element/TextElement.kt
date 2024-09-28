package ru.tensor.sbis.widget_player.converter.element

import ru.tensor.sbis.widget_player.converter.style.FormattedTextAttributes

/**
 * Интерфейс, описывающий поведение элемента, содержащего текст.
 * Любой элемент может обрабатывать текст самостоятельно, реализовав интерфейс.
 *
 * @author am.boldinov
 */
interface TextElement : TextHighlightElement {

    var textAttributes: FormattedTextAttributes

    var text: String
}