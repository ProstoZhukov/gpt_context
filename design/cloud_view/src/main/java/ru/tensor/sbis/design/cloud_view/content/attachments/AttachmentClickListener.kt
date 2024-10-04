package ru.tensor.sbis.design.cloud_view.content.attachments

import android.content.Context
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Подписка на нажатие по вложению в [CloudView]
 *
 * @author ma.kolpakov
 */
interface AttachmentClickListener {

    fun onAttachmentClicked(
        context: Context,
        attachment: MessageAttachment,
        attachments: List<MessageAttachment>
    )

    fun interface Provider : Feature {
        fun getAttachmentClickListener(): AttachmentClickListener
    }
}