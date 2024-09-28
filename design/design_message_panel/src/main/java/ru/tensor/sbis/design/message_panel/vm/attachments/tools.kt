/**
 * Вспомогательные инструменты для работы с вложениями
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.message_panel.vm.attachments

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.generated.AttachmentEvents
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsService
import java.util.*

/**
 * Подписка на обновление списка вложений.
 * Только контроллер знает, какие вложения на черновике
 */
internal fun AttachmentsService.attachmentsFlow(draftUuid: UUID) = callbackFlow {
    // загрузим вложения с черновика
    send(loadAttachments(draftUuid))
    // и подпишемся на обновления
    val subscription = setAttachmentListRefreshCallback(
        object : DataRefreshedAttachmentCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                val catalogId: String? = param[AttachmentEvents.ATTACHMENT_REFRESH_CATALOG_ID]
                if (UUIDUtils.equals(catalogId, draftUuid)) {
                    launch {
                        // что-то обновилось по черновику - перезагрузим список
                        send(loadAttachments(draftUuid))
                    }
                }
            }
        }
    )
    awaitClose { subscription.disable() }
}