package ru.tensor.sbis.widget_player.layout.widget

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.widget.controller.WidgetController

/**
 * @author am.boldinov
 */
open class GroupWidget<ELEMENT : GroupWidgetElement>(
    context: WidgetContext,
    private val renderer: GroupWidgetRenderer<ELEMENT>,
    controller: (() -> WidgetController<ELEMENT>)? = null
) : Widget<ELEMENT>(context, renderer, controller) {

    internal val children = mutableListOf<Widget<WidgetElement>>()

    internal val descendants: Sequence<Widget<WidgetElement>>
        get() = sequence {
            children.forEach { child ->
                yield(child)
                if (child is GroupWidget<*>) {
                    yieldAll(child.descendants)
                }
            }
        }

    internal fun addChild(child: Widget<WidgetElement>, parentElement: GroupWidgetElement?) {
        if (children.add(child)) {
            (renderer as? TreeWidgetRenderer<ELEMENT>)?.let {
                if (parentElement != null) {
                    it.addChild(parentElement, child.view)
                } else {
                    it.addChild(child.view)
                }
            } ?: run {
                renderer.addChild(child.view)
            }
        }
    }

    internal fun removeChild(child: Widget<WidgetElement>) {
        if (children.remove(child)) {
            renderer.removeChild(child.view)
        }
    }

    internal fun removeChildAt(index: Int): Widget<WidgetElement> {
        return children.removeAt(index).also {
            renderer.removeChildAt(index, it.view)
        }
    }

    internal fun removeAllChildren() {
        children.forEachIndexed { index, widget ->
            renderer.removeChildAt(index, widget.view)
        }
        children.clear()
    }

    override fun dispatchAttachedToPlayer() {
        super.dispatchAttachedToPlayer()
        children.forEach {
            it.dispatchAttachedToPlayer()
        }
    }

    override fun dispatchDetachedFromPlayer() {
        super.dispatchDetachedFromPlayer()
        children.forEach {
            it.dispatchDetachedFromPlayer()
        }
    }
}