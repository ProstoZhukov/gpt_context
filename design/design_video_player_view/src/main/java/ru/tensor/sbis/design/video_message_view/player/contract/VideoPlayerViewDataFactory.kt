package ru.tensor.sbis.design.video_message_view.player.contract

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания [VideoPlayerViewData].
 *
 * @author vv.chekurda
 */
interface VideoPlayerViewDataFactory : Feature {

    /**
     * Создать [VideoPlayerViewData] по [fileInfo] и [jsonObject].
     */
    fun createVideoPlayerViewData(
        uuid: UUID = UUID.randomUUID(),
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject
    ): VideoPlayerViewData

    /**
     * Создать [VideoPlayerViewData] по [jsonObject].
     */
    fun createVideoPlayerViewData(
        source: VideoSource,
        jsonObject: JSONObject
    ): VideoPlayerViewData

    /**
     * Создать [VideoPlayerViewData].
     */
    fun createVideoPlayerViewData(
        source: VideoSource,
        durationSeconds: Int
    ): VideoPlayerViewData
}