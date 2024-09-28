package ru.tensor.sbis.widget_player.widget.tabs

import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class TabContainerRenderer(
    context: WidgetContext,
    options: TabOptions
) : GroupWidgetRenderer<TabContainerElement> {

    override val view = TabContainer(context, options).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override val childrenContainer = view.childrenContainer

    override fun render(element: TabContainerElement) {
        with(view) {
            setTabData(element.data)
        }
    }
}