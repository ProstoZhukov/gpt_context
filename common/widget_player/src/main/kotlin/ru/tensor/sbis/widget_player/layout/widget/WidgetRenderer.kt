package ru.tensor.sbis.widget_player.layout.widget

import android.view.View
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.options.StyleOption

/**
 * @author am.boldinov
 */
interface WidgetRenderer<ELEMENT : WidgetElement> {

    val view: View

    fun render(element: ELEMENT)

    //suspend fun precompute(element: ELEMENT) = Unit

    fun onAttachedToPlayer() = Unit

    fun onDetachedFromPlayer() = Unit

    /**
     * Освобождает ресурсы, захваченные виджетом, перед перемещением во view pool.
     */
    fun onRecycle() = Unit

    fun onDestroy() = Unit

    //fun onInterceptStyleOption(option: StyleOption): Boolean = false
}





