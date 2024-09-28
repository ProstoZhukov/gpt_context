package ru.tensor.sbis.communicator.common.conversation

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Издатель событий отправки событий в переписке
 *
 * @author vv.chekurda
 */
class ConversationEventsPublisher {

    private val messageSentSubject = PublishSubject.create<UUID>()
    private val conversationClosedSubject = PublishSubject.create<UUID>()

    /**
     * Для подписки на события отправки сообщений.
     * Придет идентификатор диалога/чата, в котором было отправлено сообщение.
     */
    val messageSentObservable: Observable<UUID>
        get() = messageSentSubject

    /**
     * Для подписки на события закрытия экрана переписки. Придет идентификатор диалога/чата.
     */
    val conversationClosedObservable: Observable<UUID>
        get() = conversationClosedSubject

    /**
     * Известить о событии отправки сообщения
     *
     * @param conversationUuid идентификатор диалога/чата, в котором было отправлено сообщение
     */
    fun onMessageSent(conversationUuid: UUID) {
        messageSentSubject.onNext(conversationUuid)
    }

    /**
     * Известить о событии закрытия экрана переписки
     *
     * @param conversationUuid идентификатор диалога/чата, в котором было отправлено сообщение
     */
    fun onConversationClosed(conversationUuid: UUID) {
        conversationClosedSubject.onNext(conversationUuid)
    }

    /**
     * Поставщик [ConversationEventsPublisher]
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getConversationEventsPublisher(): ConversationEventsPublisher
    }
}