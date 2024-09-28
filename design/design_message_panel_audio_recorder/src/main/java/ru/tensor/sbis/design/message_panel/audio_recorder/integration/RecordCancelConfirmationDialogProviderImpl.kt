package ru.tensor.sbis.design.message_panel.audio_recorder.integration

import android.content.Context
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.RecordCancelConfirmationDialogProvider
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.design.message_panel.recorder_common.R

/**
 * Реализация поставщика [RecordCancelConfirmationDialogProvider].
 *
 * @author vv.chekurda
 */
internal object RecordCancelConfirmationDialogProviderImpl : RecordCancelConfirmationDialogProvider {

    override fun getRecordCancelConfirmationDialog(context: Context, requestCode: Int): PopupConfirmation =
        PopupConfirmation.newSimpleInstance(requestCode).also {
            it.requestTitle(context.getString(R.string.design_message_panel_recorder_confirm_cancel_recording))
            it.requestTitleMaxLines(3)
            it.requestPositiveButton(context.getString(RCommon.string.common_delete_dialog_positive))
            it.requestNegativeButton(context.getString(RCommon.string.dialog_button_cancel))
            it.setEventProcessingRequired(true)
        }
}