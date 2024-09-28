package ru.tensor.sbis.widget_player.widget.audio

import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
internal class AudioMessageElement(
    tag: String, attributes: WidgetAttributes,
    resources: WidgetResources,
    val data: AudioMessageViewData
) : WidgetElement(tag, attributes, resources)