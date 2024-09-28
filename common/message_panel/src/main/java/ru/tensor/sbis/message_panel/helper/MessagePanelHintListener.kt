package ru.tensor.sbis.message_panel.helper

import io.reactivex.internal.disposables.DisposableContainer
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.recorder.decl.RecordViewHintListener
import ru.tensor.sbis.message_panel.view.NewDialogModeHelper
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * Подписка на отображение подсказки об аудиозаписи
 *
 * @author vv.chekurda
 * @since 7/25/2019
 */
internal class MessagePanelHintListener(
    private val messageData: MessagePanelLiveData,
    disposer: DisposableContainer
) : RecordViewHintListener {

    private val newDialogModeHelper = NewDialogModeHelper(messageData, disposer)

    override fun onShowHint(show: Boolean) {
        messageData.forceHideRecipientsPanel(show)
        messageData.forceChangeAttachmentsButtonVisibility(!show)
        if (show) {
            newDialogModeHelper.onRecordStarted()
            messageData.setHint(R.string.recorder_hint_message)
        } else {
            newDialogModeHelper.onRecordCompleted()
            messageData.resetHint()
        }
    }
}