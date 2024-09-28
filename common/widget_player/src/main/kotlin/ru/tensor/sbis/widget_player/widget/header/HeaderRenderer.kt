package ru.tensor.sbis.widget_player.widget.header

import ru.tensor.sbis.design.utils.extentions.updateBottomMargin
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class HeaderRenderer(
    context: WidgetContext,
    private val options: HeaderOptions
) : GroupWidgetRenderer<HeaderElement> {

    override val view = HeaderView(context).apply {
        setDefaultWidgetLayoutParams()
    }

    override val childrenContainer = view.childrenContainer

    override fun render(element: HeaderElement) {
        options.levelOptions[element.level]?.let { levelOptions ->
            with(view) {
                levelOptions.bottomLinePaintProvider?.let { linePaint ->
                    setBottomLine(
                        padding = levelOptions.linePadding.getValuePx(context),
                        paint = linePaint.invoke(context)
                    )
                } ?: run {
                    setBottomLine(padding = 0, paint = null)
                }
                updateTopMargin(levelOptions.topMargin.getValuePx(context))
                updateBottomMargin(levelOptions.bottomMargin.getValuePx(context))
            }
        }
    }
}