package ru.tensor.sbis.widget_player.widget.spoiler

import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class SpoilerRenderer(
    context: WidgetContext,
    options: SpoilerOptions
) : GroupWidgetRenderer<SpoilerElement> {

    override val view = SpoilerView(context, options).apply {
        val verticalMargin = options.verticalMargin.getValuePx(context)
        setDefaultWidgetLayoutParams().apply {
            topMargin = verticalMargin
            bottomMargin = verticalMargin
        }
    }

    override val childrenContainer = view.childrenContainer

    override fun render(element: SpoilerElement) {
        // empty render
    }
}