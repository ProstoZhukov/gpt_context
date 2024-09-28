package ru.tensor.sbis.widget_player.widget.infoblock

import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.emoji.Emoji

/**
 * @author am.boldinov
 */
internal class InfoBlockRenderer(
    context: WidgetContext,
    private val options: InfoBlockOptions
) : GroupWidgetRenderer<InfoBlockElement> {

    override val view = InfoBlockView(context, options).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override fun render(element: InfoBlockElement) {
        with(view) {
            emoji = Emoji(
                char = element.emojiChar ?: options.emojiChar,
                size = options.emojiSize.getValue(context),
                padding = options.emojiPadding.getValuePx(context)
            )
        }
    }
}