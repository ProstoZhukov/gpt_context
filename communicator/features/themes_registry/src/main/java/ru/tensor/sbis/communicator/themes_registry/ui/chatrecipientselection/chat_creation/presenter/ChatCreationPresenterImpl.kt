package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationView
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.presenter.ChatCreationState.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Реализация презентера экрана создания нового чата
 *
 * @author vv.chekurda
 */
internal class ChatCreationPresenterImpl @Inject constructor(
    private val recipientSelectionManager: RecipientSelectionResultManager
) : ChatCreationPresenter {

    private var view: ChatCreationView? = null

    private var creationState: ChatCreationState
    private var isResumed: Boolean = false

    private val compositeDisposable = CompositeDisposable()

    init {
        creationState = PRIVATE_CHAT
        subscribeOnRecipientSelectionResult()
    }

    private fun subscribeOnRecipientSelectionResult() {
        recipientSelectionManager.getSelectionResultObservable(CHAT_CREATION_RECIPIENT_SELECTION_REQUEST_KEY)
            .subscribe(::onRecipientsSelectionDone, ::onChooseRecipientsError)
            .storeIn(compositeDisposable)
    }

    private fun onRecipientsSelectionDone(selectionResult: RecipientSelectionResult) {
        if (selectionResult.isCanceled && creationState != CHAT_SETTINGS) {
            view?.closeChatCreation()
            return
        }

        when (creationState) {
            PRIVATE_CHAT -> handlePrivateChatActions(selectionResult)
            GROUP_CHAT -> handleGroupChatActions()
            CHAT_SETTINGS -> Unit
        }
    }

    private fun handlePrivateChatActions(selectionResult: RecipientSelectionResult) {
        selectionResult.also {
            val personsUuids = it.data.allPersonsUuids
            when (personsUuids.size) {
                0 -> creationState = GROUP_CHAT
                1 -> view?.openPrivateChat(personsUuids.first())
                else -> {
                    view?.closeChatCreation()
                    Timber.e("Для одиночного выбора получателей пришло больше 1 получателя")
                }
            }
        }
    }

    private fun handleGroupChatActions() {
        creationState = CHAT_SETTINGS
        view?.showCreationChatSettings()
    }

    /**@SelfDocumented*/
    override fun onResultOk(uuid: UUID) {
        view?.openNewGroupChat(uuid)
    }

    /**@SelfDocumented*/
    override fun onResultCancel() {
        view?.closeChatCreation()
    }

    private fun onChooseRecipientsError(error: Throwable) {
        Timber.d(error, "Failed to choose recipients in ${ChatCreationPresenterImpl::class.java.simpleName}")
    }

    override fun attachView(view: ChatCreationView) {
        this.view = view
    }

    /** @SelfDocumented */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun viewIsResumed() {
        isResumed = true
    }

    /** @SelfDocumented */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun viewIsPaused() {
        isResumed = false
    }

    override fun detachView() {
        view = null
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}

private enum class ChatCreationState {
    PRIVATE_CHAT,
    GROUP_CHAT,
    CHAT_SETTINGS
}

internal const val CHAT_CREATION_RECIPIENT_SELECTION_REQUEST_KEY = "CHAT_CREATION_RECIPIENT_SELECTION_REQUEST_KEY"