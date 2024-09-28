package ru.tensor.sbis.widget_player.layout.widget

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement

/**
 * @author am.boldinov
 */
interface TreeWidgetRenderer<ELEMENT : GroupWidgetElement> : GroupWidgetRenderer<ELEMENT> {

    fun addChild(parent: GroupWidgetElement, child: View)

    override fun removeChild(child: View) {
        (child.parent as? ViewGroup)?.removeView(child)
    }

    override fun removeChildAt(index: Int, child: View) {
        // по умолчанию индекс игнорируется, т.к считается на основе корня дерева
        removeChild(child)
    }
}