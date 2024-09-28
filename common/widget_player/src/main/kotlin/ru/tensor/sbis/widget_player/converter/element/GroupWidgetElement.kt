package ru.tensor.sbis.widget_player.converter.element

import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
open class GroupWidgetElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources
) : WidgetElement(tag, attributes, resources) {

    private val _children = mutableListOf<WidgetElement>()

    val children: List<WidgetElement> = _children

    fun addChild(element: WidgetElement) {
        _children.add(element)
        element.dispatchAttachedToHierarchy(this)
        onChildAdded(element)
    }

    fun removeChild(element: WidgetElement): Boolean {
        if (_children.remove(element)) {
            element.dispatchDetachedFromHierarchy()
            onChildRemoved(element)
            return true
        }
        return false
    }

    protected open fun onChildAdded(element: WidgetElement) {

    }

    protected open fun onChildRemoved(element: WidgetElement) {

    }
}