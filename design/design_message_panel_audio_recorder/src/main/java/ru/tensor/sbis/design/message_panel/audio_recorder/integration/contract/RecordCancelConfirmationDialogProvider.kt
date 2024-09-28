package ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract

import android.content.Context
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик диалогового окна подтверждения отмены записи медиа сообщений.
 */
interface RecordCancelConfirmationDialogProvider : Feature {

    /**
     * Получить диалоговое окно подтверждения отмены записи медиа сообщений.
     */
    fun getRecordCancelConfirmationDialog(context: Context, requestCode: Int): PopupConfirmation
}