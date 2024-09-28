package ru.tensor.sbis.design.message_panel.video_recorder.integration.contract

import ru.tensor.sbis.design.message_panel.recorder_common.integration.RecorderDelegate
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordResultData
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import ru.tensor.sbis.message_panel.view.MessagePanel

/**
 * Делегат компонента записи видеосообщений [VideoRecordView] для подключения к панели сообщений [MessagePanel].
 *
 * @author vv.chekurda
 */
interface VideoRecorderDelegate : RecorderDelegate<VideoRecordViewState, VideoRecordResultData>