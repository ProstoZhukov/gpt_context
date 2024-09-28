package ru.tensor.sbis.widget_player.converter.element

import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.*

/**
 * @author am.boldinov
 */
open class WidgetElement(
    val tag: String,
    val attributes: WidgetAttributes,
    internal val resources: WidgetResources
) {

    var style: StyleProperties = resources.globalStyle
        private set

    var styleReducer: (StylePropertiesBuilder.() -> Unit)? = null
        set(value) {
            field = value
            reduceStyle()
        }

    val id = attributes.get(WidgetAttributes.ID)?.let { WidgetID(it) } ?: WidgetID.generate()

    val type = "$tag${javaClass.name}".hashCode()

    val isAttachedToHierarchy get() = parent != null

    val indexInParent get() = (parent as? GroupWidgetElement)?.children?.indexOf(this) ?: -1

    var parent: WidgetElement? = null
        private set

    fun applyStyle(style: StyleProperties) {
        reduceStyle(style)
    }

    internal fun dispatchAttachedToHierarchy(parent: WidgetElement) {
        this.parent = parent
        onAttachedToHierarchy(parent)
        reduceStyle()
    }

    internal fun dispatchDetachedFromHierarchy() {
        onDetachedFromHierarchy()
        this.parent = null
    }

    protected open fun onAttachedToHierarchy(parent: WidgetElement) {

    }

    protected open fun onDetachedFromHierarchy() {

    }

    protected open fun onStyleChanged(style: StyleProperties) {

    }

    private fun reduceStyle(target: StyleProperties = (parent?.style ?: style)) {
        val oldStyle = style
        style = target
        styleReducer?.let { reducer ->
            style = style.reduce(reducer)
        }
        if (oldStyle !== style) {
            onStyleChanged(style)
        }
    }
}