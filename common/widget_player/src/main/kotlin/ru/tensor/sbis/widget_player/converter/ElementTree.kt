package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
class ElementTree(
    val root: GroupWidgetElement,
    elements: Map<WidgetID, WidgetElement>? = null
) {

    // TODO Добавить RootElement для отслеживания изменений в иерархии и пересчета
    private val elements: Map<WidgetID, WidgetElement> by lazy(LazyThreadSafetyMode.NONE) {
        elements ?: mutableMapOf<WidgetID, WidgetElement>().apply {
            fill(this, root)
        }
    }

    fun count(): Int {
        return elements.size
    }

    fun findElementById(id: WidgetID): WidgetElement? {
        return elements[id]
    }

    fun <ELEMENT> findElementsByType(type: Class<ELEMENT>): List<ELEMENT> {
        return mutableListOf<ELEMENT>().apply {
            elements.values.forEach {
                if (type.isInstance(it)) {
                    @Suppress("UNCHECKED_CAST")
                    add(it as ELEMENT)
                }
            }
        }
    }

    fun findElementsByTag(tag: String): List<WidgetElement> {
        return elements.values.filter {
            it.tag == tag
        }
    }

    private fun fill(map: MutableMap<WidgetID, WidgetElement>, root: GroupWidgetElement) {
        map[root.id] = root
        root.children.forEach {
            if (it is GroupWidgetElement) {
                fill(map, it)
            } else {
                map[it.id] = it
            }
        }
    }
}