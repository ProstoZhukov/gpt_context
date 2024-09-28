package ru.tensor.sbis.communicator.sbis_conversation.ui

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationDataDispatcher
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import java.util.*

/**
 * Диспетчер событий в реестре сообщений
 */
internal class ConversationDataDispatcher
    : BaseConversationDataDispatcher<ConversationMessage, ConversationState, ConversationData>() {

    /**
     * BehaviorSubject, передающий состояния экрана переписки
     */
    override val conversationStateSubject = BehaviorSubject.createDefault(ConversationState())

    private val addRecipientSubject = PublishSubject.create<UUID>()
    /**
     * PublishSubject, передающий UUID получателя сообщения
     */
    val addRecipientObservable = addRecipientSubject.prepareObservable()

    private val createTaskEventSubject = PublishSubject.create<Unit>()
    /**
     * PublishSubject, передающий событие создания задачи
     */
    val createTaskEventObservable = createTaskEventSubject.prepareObservable()

    /** @SelfDocumented */
    fun addRecipient(recipientUuid: UUID) {
        addRecipientSubject.onNext(recipientUuid)
    }

    /** @SelfDocumented */
    fun createTaskEvent() {
        createTaskEventSubject.onNext(Unit)
    }
}