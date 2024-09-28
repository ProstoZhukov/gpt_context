@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.toolbar

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.event.UnreadCountEvent
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationToolbarView
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.BaseCoreConversationInfo
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationDataDispatcher
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData
import ru.tensor.sbis.mvp.presenter.AbstractBasePresenter

/**
 * Базовая реализация делегата презентера по тулбару переписки.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationToolbarPresenter<VIEW : BaseConversationToolbarView<MESSAGE>,
    INTERACTOR : BaseConversationInteractor<*>,
    MESSAGE : BaseConversationMessage,
    STATE : BaseConversationState<MESSAGE>,
    DATA : BaseConversationData,
    INFO : BaseCoreConversationInfo,
    DISPATCHER : BaseConversationDataDispatcher<MESSAGE, STATE, DATA>>(
        protected val interactor: INTERACTOR,
        protected val coreConversationInfo: INFO,
        protected val dataDispatcher: DISPATCHER
) : AbstractBasePresenter<VIEW, Pair<UnreadCountEvent.EventType, HashMap<String, String>>>(null),
    BaseConversationToolbarPresenterContract<VIEW> {

    /** Модель с данными для отображения шапки в реестре сообщений. */
    protected var toolbarData: ToolbarData? = null

    /** Модель разрешений и признаков доступности переписки */
    protected var conversationAccess: ConversationAccess = ConversationAccess()

    /** Данные о печатающих пользователях. */
    protected var typingData: UsersTypingData = UsersTypingData()

    /** Состояние сообщений переписки */
    protected val conversationState: STATE
        get() = dataDispatcher.getConversationState()

    /**@SelfDocumented*/
    protected val compositeDisposable = CompositeDisposable()

    /**@SelfDocumented*/
    protected val typingDisposable = SerialDisposable().apply {
        compositeDisposable.add(this)
    }

    /**@SelfDocumented*/
    protected var isResumed: Boolean = false
        private set

    private var isSubscribed = false

    @CallSuper
    override fun attachView(view: VIEW) {
        // важно вызвать перед получением событий от родительского презентера
        super.attachView(view)
        if (!isSubscribed) {
            subscribeOnDataUpdate()
            isSubscribed = true
        }
        displayViewState(view)
    }

    override fun viewIsStarted() = Unit

    override fun viewIsStopped() = Unit

    override fun viewIsResumed() {
        isResumed = true
    }

    override fun viewIsPaused() {
        isResumed = false
    }

    /**@SelfDocumented*/
    @CallSuper
    protected open fun subscribeOnDataUpdate() {
        dataDispatcher.run {
            compositeDisposable.addAll(
                conversationDataObservable.subscribe(::handleDataWithUpdate),
                conversationEventObservable.subscribe(::handleConversationEvent),
                conversationStateObservable.subscribe {
                    handleConversationStateChanges(currentState = it.first, newState = it.second)
                }
            )
        }
        subscribeOnTypingUsers()
    }

    /**@SelfDocumented*/
    protected open fun subscribeOnTypingUsers() {
        interactor.observeTypingUsers()
            .subscribe(::onTypingUsersChanged)
            .storeIn(typingDisposable)
    }

    /**@SelfDocumented*/
    protected open fun onTypingUsersChanged(users: List<String>) {
        typingData = UsersTypingData(users)
        mView?.setTypingUsers(typingData)
    }

    private fun handleDataWithUpdate(conversationData: DATA) {
        handleConversationDataChanges(conversationData)
        mView?.let { displayViewState(it) }
    }

    /**@SelfDocumented*/
    @CallSuper
    protected open fun handleConversationDataChanges(conversationData: DATA) {
        toolbarData = conversationData.toolbarData
        conversationAccess = conversationData.conversationAccess
    }

    /**@SelfDocumented*/
    @CallSuper
    protected open fun handleConversationEvent(event: ConversationEvent) {
        if (event == ConversationEvent.UPDATE_VIEW) {
            mView?.let { displayViewState(it) }
        } else if (event == ConversationEvent.DIALOG_CREATED) {
            subscribeOnTypingUsers()
        }
    }

    /**@SelfDocumented*/
    protected open fun handleConversationStateChanges(currentState: STATE?, newState: STATE) = Unit

    /**@SelfDocumented*/
    override fun onToolbarClick() = Unit

    /**@SelfDocumented*/
    protected open fun insertTitleData(view: VIEW) {
        if (conversationAccess.isAvailable) toolbarData?.let(view::setToolbarData)
    }

    /**@SelfDocumented*/
    @CallSuper
    override fun displayViewState(view: VIEW) {
        insertTitleData(view)
        view.setTypingUsers(typingData)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}