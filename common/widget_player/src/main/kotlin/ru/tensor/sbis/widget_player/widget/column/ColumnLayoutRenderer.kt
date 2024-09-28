package ru.tensor.sbis.widget_player.widget.column

import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class ColumnLayoutRenderer(
    context: WidgetContext,
    options: ColumnLayoutOptions
) : GroupWidgetRenderer<ColumnLayoutElement> {

    override val view = ColumnLayoutView(context, options).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override val childrenContainer = view.childrenContainer

    override fun render(element: ColumnLayoutElement) {
        with(view) {
            setChildrenProportions(element.childrenProportions)
        }
    }
}