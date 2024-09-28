package ru.tensor.sbis.design.video_message_view.message.data

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.communication_decl.communicator.media.getRecognized
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewDataFactoryImpl.createVideoPlayerViewData
import java.util.UUID

/**
 * Реализация фабрики [VideoMessageViewDataFactory].
 *
 * @author vv.chekurda
 */
internal object VideoMessageViewDataFactoryImpl : VideoMessageViewDataFactory {

    override fun createVideoMessageViewData(
        uuid: UUID,
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject,
        recognizedText: CharSequence?,
        isEdited: Boolean
    ): VideoMessageViewData =
        VideoMessageViewData(
            playerData = createVideoPlayerViewData(
                uuid = uuid,
                fileInfo = fileInfo,
                jsonObject = jsonObject
            ),
            recognizedText = recognizedText,
            recognized = jsonObject.getRecognized(),
            isEdited = isEdited
        )

    override fun createVideoMessageViewData(
        source: VideoSource,
        jsonObject: JSONObject,
        recognizedText: CharSequence?,
        isEdited: Boolean
    ): VideoMessageViewData =
        VideoMessageViewData(
            playerData = createVideoPlayerViewData(
                source = source,
                jsonObject = jsonObject
            ),
            recognizedText = recognizedText,
            recognized = jsonObject.getRecognized(),
            isEdited = isEdited
        )

    override fun createVideoMessageViewData(
        source: VideoSource,
        durationSeconds: Int,
        recognizedText: CharSequence?,
        recognized: Boolean?,
        isEdited: Boolean
    ): VideoMessageViewData =
        VideoMessageViewData(
            playerData = createVideoPlayerViewData(
                source = source,
                durationSeconds = durationSeconds,
            ),
            recognizedText = recognizedText,
            recognized = recognized,
            isEdited = isEdited
        )
}