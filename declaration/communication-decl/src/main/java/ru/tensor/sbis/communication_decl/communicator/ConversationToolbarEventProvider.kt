package ru.tensor.sbis.communication_decl.communicator

import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.communicator.event.ConversationToolbarEvent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс, подоставляющий систему событий [ConversationToolbarEvent] тулбара диалогов/чатов.
 *
 * @author vv.chekurda
 */
interface ConversationToolbarEventProvider : Feature {

    /**
     * Функция, возвращающая систему событий, по которой можно настроить тулбар диалога/чата по событиям [ConversationToolbarEvent]
     * при получении и изменении данных о переписке
     * @return [Observable] для подписки на [ConversationToolbarEvent]
     */
    fun getConversationToolbarEventObservable() : Observable<ConversationToolbarEvent>

}