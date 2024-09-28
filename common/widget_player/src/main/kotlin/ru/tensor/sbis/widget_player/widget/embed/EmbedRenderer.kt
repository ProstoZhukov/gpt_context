package ru.tensor.sbis.widget_player.widget.embed

import android.widget.FrameLayout
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.embed.youtube.YouTubeElement
import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphOptions
import ru.tensor.sbis.widget_player.widget.text.FormattedTextElement

/**
 * @author am.boldinov
 */
internal class EmbedRenderer(
    context: WidgetContext,
    paragraphOptions: ParagraphOptions,
    private val youTubeRenderer: Lazy<WidgetRenderer<YouTubeElement>>,
    private val textLinkRenderer: Lazy<WidgetRenderer<FormattedTextElement>>
) : WidgetRenderer<EmbedElement> {

    override val view = FrameLayout(context).apply {
        val verticalMargin = paragraphOptions.verticalMargin.getValuePx(context)
        minimumHeight = paragraphOptions.minHeight.getValuePx(context)
        setDefaultWidgetLayoutParams().apply {
            topMargin = verticalMargin
            bottomMargin = verticalMargin
        }
    }

    override fun render(element: EmbedElement) {
        view.removeAllViews()
        val renderer = when (element.content) {
            is YouTubeElement -> youTubeRenderer.value.also {
                it.render(element.content)
            }
            is FormattedTextElement -> textLinkRenderer.value.also {
                it.render(element.content)
            }
            else -> null
        }
        if (renderer != null) {
            view.addView(renderer.view)
        }
    }
}