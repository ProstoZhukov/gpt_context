package ru.tensor.sbis.design.message_panel.video_recorder.integration.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.design.message_panel.video_recorder.view.VideoRecordView
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания делегата записи видеосообщений [VideoRecorderDelegate].
 *
 * @author vv.chekurda
 */
interface VideoRecorderDelegateFactory : Feature {

    /**
     * Создать делегата записи видеосообщений [VideoRecorderDelegate].
     */
    fun createRecorderDelegate(
        fragment: Fragment,
        messagePanel: MessagePanel,
        messagePanelController: MessagePanelController<*, *, *>,
        videoRecordView: VideoRecordView,
        headerDateView: HeaderDateView? = null
    ): VideoRecorderDelegate
}