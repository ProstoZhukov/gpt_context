package ru.tensor.sbis.design.video_message_view.message.contract

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания [VideoMessageViewData].
 *
 * @author vv.chekurda
 */
interface VideoMessageViewDataFactory : Feature {

    /**
     * Создать [VideoMessageViewData] по [fileInfo] и [jsonObject].
     */
    fun createVideoMessageViewData(
        uuid: UUID = UUID.randomUUID(),
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject,
        recognizedText: CharSequence?,
        isEdited: Boolean = false
    ): VideoMessageViewData

    /**
     * Создать [VideoMessageViewData] по [jsonObject].
     */
    fun createVideoMessageViewData(
        source: VideoSource,
        jsonObject: JSONObject,
        recognizedText: CharSequence?,
        isEdited: Boolean = false
    ): VideoMessageViewData

    /**
     * Создать [VideoMessageViewData].
     */
    fun createVideoMessageViewData(
        source: VideoSource,
        durationSeconds: Int,
        recognizedText: CharSequence? = null,
        recognized: Boolean? = false,
        isEdited: Boolean = false
    ): VideoMessageViewData
}