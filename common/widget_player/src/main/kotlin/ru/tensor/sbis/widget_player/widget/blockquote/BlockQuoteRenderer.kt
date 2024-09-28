package ru.tensor.sbis.widget_player.widget.blockquote

import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class BlockQuoteRenderer(
    private val context: WidgetContext
) : GroupWidgetRenderer<BlockQuoteElement> {

    override val view = BlockQuoteView(context).apply {
        val verticalMargin = Offset.M.getDimenPx(context)
        setDefaultWidgetLayoutParams().apply {
            topMargin = verticalMargin
            bottomMargin = verticalMargin
        }
    }

    override fun render(element: BlockQuoteElement) {
        view.setLineColor(element.style.textColor.getValue(context))
    }
}