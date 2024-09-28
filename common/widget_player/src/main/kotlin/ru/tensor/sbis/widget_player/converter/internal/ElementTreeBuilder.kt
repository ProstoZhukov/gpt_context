package ru.tensor.sbis.widget_player.converter.internal

import ru.tensor.sbis.widget_player.converter.ElementTree
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import java.util.LinkedList

/**
 * @author am.boldinov
 */
internal class ElementTreeBuilder {

    private val queue = LinkedList<WidgetElement>()
    private val elements = mutableMapOf<WidgetID, WidgetElement>()

    fun beginElement(element: WidgetElement) {
        queue.peekLast()?.let {
            (it as? GroupWidgetElement) ?: (it.parent as? GroupWidgetElement)
        }?.addChild(element)
        queue.add(element)
        elements[element.id] = element
    }

    fun commitElement() {
        if (queue.size > 1) { // save root element
            queue.pollLast()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> element(clazz: Class<T>): T? {
        return queue.peekLast()?.takeIf {
            clazz.isInstance(it)
        } as? T
    }

    fun build(): ElementTree {
        val root = queue.pop() as GroupWidgetElement
        queue.clear()
        val elementsCopy = elements.toMap()
        elements.clear()
        return ElementTree(
            root = root,
            elements = elementsCopy
        )
    }
}