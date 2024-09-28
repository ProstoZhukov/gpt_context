@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Базовая реализация шины событий между делегатами презентера переписки.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationDataDispatcher<
    MESSAGE : BaseConversationMessage,
    STATE : BaseConversationState<*>,
    DATA : BaseConversationData> {

    protected abstract val conversationStateSubject: BehaviorSubject<STATE>

    private val conversationDataSubject = BehaviorSubject.create<DATA>()
    private val conversationEventSubject = PublishSubject.create<ConversationEvent>()
    private val editMessageSubject = PublishSubject.create<MESSAGE>()

    val conversationStateObservable: Observable<Pair<STATE?, STATE>> by lazy {
        conversationStateSubject.map {
            @Suppress("UNCHECKED_CAST") val copy = it.copy() as STATE
            Pair<STATE?, STATE>(null, copy)
        }
            .scan { previous, current -> previous.second to current.second }
    }
    val conversationDataObservable = conversationDataSubject.prepareObservable()
    val conversationEventObservable = conversationEventSubject.prepareObservable()
    val editMessageObservable = editMessageSubject.prepareObservable()

    /** @SelfDocumented */
    fun updateData(conversationData: DATA) {
        conversationDataSubject.onNext(conversationData)
    }

    /** @SelfDocumented */
    fun getConversationData(): DATA? = conversationDataSubject.value

    /** @SelfDocumented */
    fun sendConversationEvent(conversationEvent: ConversationEvent) {
        conversationEventSubject.onNext(conversationEvent)
    }

    /** @SelfDocumented */
    fun updateConversationState(state: STATE) {
        conversationStateSubject.onNext(state)
    }

    /** @SelfDocumented */
    fun getConversationState(): STATE = conversationStateSubject.value!!

    protected fun <T> Subject<T>.prepareObservable(): Observable<T> = this
}

/**
 * Enum событий в реестре сообщений
 */
enum class ConversationEvent {
    CHAT_CREATED,
    DIALOG_CREATED,
    THREAD_DRAFT_CREATED,
    SELECT_THREAD_PARTICIPANTS,
    SHOW_THREAD_CREATION,
    UPDATE_DATA_LIST,
    EDIT_MESSAGE,
    QUOTE_MESSAGE,
    STOP_EDITING,
    SAVE_RECIPIENTS,
    SELECT_RECIPIENTS,
    UPDATE_VIEW,
    BLOCK_MESSAGE_SENDING,
    SABY_CHAT_CREATED,
    SABY_CHAT_IS_EXISTS,
    CRM_CHAT_CREATED,
    CRM_CHAT_IS_EXISTS
}