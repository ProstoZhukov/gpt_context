package ru.tensor.sbis.message_panel.decl

import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик контроллера вложений
 *
 * @author kv.martyshenko
 */
fun interface AttachmentControllerProvider : Feature {
    fun getAttachmentController(): DependencyProvider<Attachment>
}