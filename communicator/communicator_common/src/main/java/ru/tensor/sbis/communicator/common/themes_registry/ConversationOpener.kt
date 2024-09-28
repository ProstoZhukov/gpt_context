package ru.tensor.sbis.communicator.common.themes_registry

import java.util.UUID

/**
 * Контракт, определяющий возможность открытия реестра диалогов или чатов с последующим открытием переписки на конкретном сообщении
 * @author da.zhukov
 */
interface ConversationOpener {
    /**
     * Открыть реестр диалогов или чатов с дальнейшим переходом к сообщению из переписки
     * @param selectedConversationUuid UUID переписки для перехода
     * @param selectedMessageUuid      UUID сообщения для перехода
     * @param resetTypeIfUnanswered    нужно ли сбросить фильтр *мне не ответили*
     */
    fun resetStateForNewData(
        selectedConversationUuid: UUID?,
        selectedMessageUuid: UUID?,
        resetTypeIfUnanswered: Boolean = false
    )
}