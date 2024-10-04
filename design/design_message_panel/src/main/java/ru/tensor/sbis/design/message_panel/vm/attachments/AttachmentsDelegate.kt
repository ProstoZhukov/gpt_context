package ru.tensor.sbis.design.message_panel.vm.attachments

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.attachments.ui.view.register.contract.AttachmentsActionListener
import java.util.*

/**
 * Внутренний API для работы с вложениями панели ввода
 *
 * @author ma.kolpakov
 */
internal interface AttachmentsDelegate : MessagePanelAttachmentsApi, AttachmentsActionListener {

    val attachmentsUuid: StateFlow<List<UUID>>

    fun attachAttachmentsScope(scope: CoroutineScope)

    fun clearAttachments()
}
