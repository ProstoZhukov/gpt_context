package ru.tensor.sbis.widget_player.widget.video

import android.net.Uri
import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotNull

/**
 * @author am.boldinov
 */
internal class VideoMessageElementFactory(
    private val viewDataFactory: VideoMessageViewDataFactory
) : WidgetElementFactory<VideoMessageElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): VideoMessageElement {
        val json = JSONObject().apply {
            attributes.keySet().forEach {
                put(it, attributes.get(it))
            }
        }
        val uri = attributes.getNotNull("uri")
        val sourceData = SourceData.UriData(Uri.parse(uri))
        val data = viewDataFactory.createVideoMessageViewData(
            source = MediaSource.VideoSource(
                data = sourceData
            ),
            jsonObject = json,
            recognizedText = attributes.get("transcription")
        )
        return VideoMessageElement(tag, attributes, environment.resources, data)
    }
}