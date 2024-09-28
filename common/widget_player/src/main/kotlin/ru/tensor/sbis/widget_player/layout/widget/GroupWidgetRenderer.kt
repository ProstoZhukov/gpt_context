package ru.tensor.sbis.widget_player.layout.widget

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement

/**
 * @author am.boldinov
 */
interface GroupWidgetRenderer<ELEMENT : GroupWidgetElement> : WidgetRenderer<ELEMENT> {

    val childrenContainer: ViewGroup get() = view as ViewGroup

    fun addChild(child: View) {
        childrenContainer.addView(child)
    }

    fun removeChild(child: View) {
        childrenContainer.removeView(child)
    }

    fun removeChildAt(index: Int, child: View) {
        childrenContainer.removeViewAt(index)
    }
}