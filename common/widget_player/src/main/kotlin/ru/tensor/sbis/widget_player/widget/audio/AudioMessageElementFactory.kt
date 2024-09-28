package ru.tensor.sbis.widget_player.widget.audio

import android.net.Uri
import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotNull

/**
 * @author am.boldinov
 */
internal class AudioMessageElementFactory(
    private val viewDataFactory: AudioMessageViewDataFactory
) : WidgetElementFactory<AudioMessageElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): AudioMessageElement {
        val json = JSONObject().apply {
            attributes.keySet().forEach {
                put(it, attributes.get(it))
            }
        }
        val uri = attributes.getNotNull("uri")
        val sourceData = SourceData.UriData(Uri.parse(uri))
        val data = viewDataFactory.createAudioMessageViewData(
            source = MediaSource.AudioSource(
                data = sourceData
            ),
            jsonObject = json,
            recognizedText = attributes.get("transcription")
        )
        return AudioMessageElement(tag, attributes, environment.resources, data)
    }
}