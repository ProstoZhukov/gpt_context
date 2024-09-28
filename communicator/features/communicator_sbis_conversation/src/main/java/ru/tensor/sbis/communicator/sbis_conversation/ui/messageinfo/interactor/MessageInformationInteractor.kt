package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.interactor

import io.reactivex.Observable
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import java.util.*

/**
 * Интерактор экрана информации о сообщении
 *
 * @author vv.chekurda
 */
internal interface MessageInformationInteractor {

    /**
     * Получить модель сообщения
     * @param messageUuid идентификатор сообщения
     */
    fun getMessage(messageUuid: UUID): Observable<ConversationMessage>

    /**
     * Очистить ссылки на объекты
     */
    fun clearReferences()
}