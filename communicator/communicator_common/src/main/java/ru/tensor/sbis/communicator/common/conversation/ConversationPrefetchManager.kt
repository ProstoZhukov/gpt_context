package ru.tensor.sbis.communicator.common.conversation

import io.reactivex.Completable
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Интерфейс для предварительной загрузки переписки из реестра диалогов / каналов
 */
interface ConversationPrefetchManager : Feature {

    /** @SelfDocumented **/
    fun prefetch(
        themeUuid: UUID,
        documentUuid: UUID?,
        relevantMessageUUID: UUID?,
        isGroupConversation: Boolean,
        isChat: Boolean,
        isConsultation: Boolean
    ): Completable

    /** @SelfDocumented **/
    fun isReady(themeUuid: UUID): Boolean

    /** @SelfDocumented **/
    fun clear()

    /** @SelfDocumented **/
    interface Provider : Feature {
        val prefetchManager: ConversationPrefetchManager
    }
}