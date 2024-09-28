package ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communication_decl.communicator.event.ConversationToolbarEvent

/**
 * Менеджер событий тулбара реестра сообщений
 */
class ConversationToolbarEventManagerImpl : ConversationToolbarEventManager {

    private val titleEventSubject = BehaviorSubject.create<ConversationToolbarEvent>()

    /** @SelfDocumented */
    override val hasObservers: Boolean
        get() = titleEventSubject.hasObservers()

    /** @SelfDocumented */
    override fun postEvent(event: ConversationToolbarEvent) {
        titleEventSubject.onNext(event)
    }

    /** @SelfDocumented */
    override fun getConversationToolbarEventObservable(): Observable<ConversationToolbarEvent> =
        titleEventSubject.distinctUntilChanged()

}