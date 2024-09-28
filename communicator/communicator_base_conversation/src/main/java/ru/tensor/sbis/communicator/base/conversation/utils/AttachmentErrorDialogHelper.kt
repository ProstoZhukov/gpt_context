package ru.tensor.sbis.communicator.base.conversation.utils

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.design.R as RDesignCommunicator

/**
 * Вспомогательная реализация для окон ошибок вложений.
 *
 * @author vv.chekurda
 */
object AttachmentErrorDialogHelper {

    fun getConfirmationDialogTitle(resourceProvider: ResourceProvider, errorMessage: String): String {
        val resultErrorMessage = resourceProvider.getString(ru.tensor.sbis.communicator.design.R.string.communicator_confirmation_dialog_attachment_error_stub_text)
        return "$resultErrorMessage.\n${resourceProvider.getString(RDesignCommunicator.string.communicator_confirmation_dialog_repeat_title)}"
    }
}