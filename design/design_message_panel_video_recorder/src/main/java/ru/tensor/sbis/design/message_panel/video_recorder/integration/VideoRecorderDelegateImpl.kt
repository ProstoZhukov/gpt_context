package ru.tensor.sbis.design.message_panel.video_recorder.integration

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.design.message_panel.recorder_common.integration.BaseRecorderDelegate
import ru.tensor.sbis.design.message_panel.video_recorder.MessagePanelVideoRecorderPlugin.callStateProvider
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegate
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegateFactory
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordResultData
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData
import ru.tensor.sbis.message_panel.helper.media.VideoRecordData

/**
 * Реализация делегата компонента записи видеосообщений [VideoRecorderDelegate].
 *
 * @author vv.chekurda
 */
internal class VideoRecorderDelegateImpl private constructor(
    fragment: Fragment,
    messagePanel: MessagePanel,
    messagePanelController: MessagePanelController<*, *, *>,
    private val videoRecordView: VideoRecordView,
    private val headerDateView: HeaderDateView? = null
) : BaseRecorderDelegate<VideoRecordViewState, VideoRecordResultData>(
    fragment = fragment,
    messagePanel = messagePanel,
    messagePanelController = messagePanelController,
    recorder = videoRecordView,
    callStateProvider = callStateProvider,
    isVideo = true
), VideoRecorderDelegate {

    companion object : VideoRecorderDelegateFactory {
        override fun createRecorderDelegate(
            fragment: Fragment,
            messagePanel: MessagePanel,
            messagePanelController: MessagePanelController<*, *, *>,
            videoRecordView: VideoRecordView,
            headerDateView: HeaderDateView?,
        ): VideoRecorderDelegate =
            VideoRecorderDelegateImpl(
                fragment = fragment,
                messagePanel = messagePanel,
                messagePanelController = messagePanelController,
                videoRecordView = videoRecordView,
                headerDateView = headerDateView
            )
    }

    override fun getMediaRecordData(resultData: VideoRecordResultData): MediaRecordData =
        VideoRecordData(
            resultData.videoFile,
            resultData.duration
        )

    override fun onRecordCompleted(resultData: VideoRecordResultData) {
        super.onRecordCompleted(resultData)
        resetHeaderDateViewTranslationDelayed()
    }

    override fun onRecordStarted() {
        super.onRecordStarted()
        headerDateView?.also { it.translationZ = -it.elevation }
    }

    override fun onRecordCanceled() {
        super.onRecordCanceled()
        resetHeaderDateViewTranslationDelayed()
    }

    override fun onRecordError(error: java.lang.Exception) {
        super.onRecordError(error)
        resetHeaderDateViewTranslationDelayed()
    }

    private fun resetHeaderDateViewTranslationDelayed() {
        headerDateView?.postDelayed({
            headerDateView.translationZ = 0f
        }, HEADER_DATE_TRANSLATION_DELAY_MS)
    }
}

private const val HEADER_DATE_TRANSLATION_DELAY_MS = 240L