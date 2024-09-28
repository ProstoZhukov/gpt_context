package ru.tensor.sbis.widget_player.widget.link

import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkData
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class DecoratedLinkElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val linkData: DecoratedLinkData,
    val linkType: DecoratedLinkType
) : GroupWidgetElement(tag, attributes, resources) {

    override fun onChildAdded(element: WidgetElement) {
        super.onChildAdded(element)
        // вынужденное решение, т.к сервер может добавлять к ссылкам чайлдов
        // оформить позже более красивое решение для игнорирования чайлдов
        removeChild(element)
    }
}