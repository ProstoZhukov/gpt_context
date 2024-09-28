package ru.tensor.sbis.design.video_message_view.player.data

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaPlayerFileInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.communication_decl.communicator.media.getDuration
import ru.tensor.sbis.design.video_message_view.player.contract.VideoPlayerViewDataFactory
import ru.tensor.sbis.design.video_message_view.player.utils.VideoPlayerUtils
import java.util.UUID

/**
 * Реализация фабрики [VideoPlayerViewDataFactory].
 *
 * @author vv.chekurda
 */
internal object VideoPlayerViewDataFactoryImpl : VideoPlayerViewDataFactory {

    override fun createVideoPlayerViewData(
        uuid: UUID,
        fileInfo: MediaPlayerFileInfo,
        jsonObject: JSONObject,
    ): VideoPlayerViewData =
        VideoPlayerViewData(
            videoSource = VideoPlayerUtils.createVideoSource(uuid, fileInfo),
            durationSeconds = jsonObject.getDuration(),
            previewVM = VideoPlayerUtils.createVideoPlayerPreviewVM(fileInfo)
        )

    override fun createVideoPlayerViewData(
        source: VideoSource,
        jsonObject: JSONObject
    ): VideoPlayerViewData =
        createVideoPlayerViewData(
            source = source,
            durationSeconds = jsonObject.getDuration(),
        )

    override fun createVideoPlayerViewData(
        source: VideoSource,
        durationSeconds: Int
    ): VideoPlayerViewData =
        VideoPlayerViewData(
            videoSource = source,
            durationSeconds = durationSeconds,
            previewVM = VideoPlayerUtils.createVideoPlayerPreviewVM(source)
        )
}