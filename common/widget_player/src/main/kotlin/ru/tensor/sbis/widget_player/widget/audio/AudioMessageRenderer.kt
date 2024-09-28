package ru.tensor.sbis.widget_player.widget.audio

import ru.tensor.sbis.design.audio_player_view.view.message.AudioMessageView
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class AudioMessageRenderer(
    context: WidgetContext,
    private val options: AudioMessageOptions
) : WidgetRenderer<AudioMessageElement> {

    override val view = AudioMessageView(context).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override fun render(element: AudioMessageElement) {
        view.data = element.data
    }
}