package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.attachments.ui.view.clickhandler.AttachmentClickHandler
import ru.tensor.sbis.design.cloud_view.CloudViewPlugin.defaultAttachmentClickListener
import ru.tensor.sbis.design.cloud_view.model.AttachmentCloudContent

/**
 * Общий обработчик нажатия на элементы коллажа вложений. При нажатии на вложение вызывает соответсвующий ему
 * специфичный обработчик.
 *
 * @author us.bessonov
 */
internal class AttachmentCollageItemClickHandler(
    private val context: Context,
    private val attachments: List<AttachmentCloudContent>
) : AttachmentClickHandler {

    override fun onAttachmentClick(attachmentModel: AttachmentModel) {
        with(attachments.first { it.attachment == attachmentModel }) {
            val listener = listener ?: defaultAttachmentClickListener
            listener?.onAttachmentClicked(context, attachment, attachments.map { it.attachment })
        }
    }
}