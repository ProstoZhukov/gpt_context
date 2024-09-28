package ru.tensor.sbis.communicator.common.navigation.data

import org.apache.commons.lang3.StringUtils.EMPTY
import java.util.UUID

/**
 * Параметры для открытия обсуждения статьи через навигацию модуля коммуникатор
 *
 * @param documentUuid  идентификатор документа
 * @param dialogUuid    идентификатор диалога
 * @param messageUuid   идентификатор сообщения
 * @param documentUrl   url документа (опционально, для открытия в веб-вью)
 * @param documentTitle заголовок документа (опционально, для открытия в веб-вью)
 * @param isSocnetEvent true, если релевантное сообщение - событие соц.сети (вас упомянули и тд.)
 *
 * @author vv.chekurda
 */
data class CommunicatorArticleDiscussionParams(
    val documentUuid: UUID,
    val dialogUuid: UUID,
    val messageUuid: UUID?,
    val documentUrl: String = EMPTY,
    val documentTitle: String? = null,
    val isSocnetEvent: Boolean = false
)