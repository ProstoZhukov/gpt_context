package ru.tensor.sbis.widget_player.widget.audio

import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal class AudioMessageWidgetComponent(
    private val viewDataFactory: AudioMessageViewDataFactory
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = AudioMessageElementFactory(viewDataFactory),
        inflater = {
            Widget(
                context = this,
                renderer = AudioMessageRenderer(this, audioMessageOptions)
            )
        }
    )
}