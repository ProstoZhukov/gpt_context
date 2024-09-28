package ru.tensor.sbis.widget_player.layout.widget

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */

fun <ELEMENT : WidgetElement> viewRenderer(block: () -> View): WidgetRenderer<ELEMENT> {
    return object : WidgetRenderer<ELEMENT> {

        override val view = block.invoke()

        override fun render(element: ELEMENT) {
            // ignore
        }
    }
}

fun <ELEMENT : GroupWidgetElement> viewGroupRenderer(block: () -> ViewGroup): GroupWidgetRenderer<ELEMENT> {
    return object : GroupWidgetRenderer<ELEMENT> {
        override val view = block.invoke()

        override fun render(element: ELEMENT) {
            // ignore
        }
    }
}