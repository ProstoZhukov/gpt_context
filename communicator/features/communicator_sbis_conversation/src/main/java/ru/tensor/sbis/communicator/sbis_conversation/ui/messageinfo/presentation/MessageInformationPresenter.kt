package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation

import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.data.MessageInformationModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.interactor.MessageInformationInteractor

/**
 * Реализация презентера экрана информации о сообщении
 *
 * @param interactor  интерактор информации о сообщении
 * @param messageInfo модель информации о сообщении
 */
internal class MessageInformationPresenterImpl(
    private val interactor: MessageInformationInteractor,
    private val messageInfo: MessageInformationModel
) : MessageInformationPresenter {

    private var view: MessageInformationView? = null
    private var message: ConversationMessage? = null
    private var disposer = CompositeDisposable()

    init {
        loadMessage()
    }

    override fun attachView(view: MessageInformationView) {
        this.view = view
        displayViewState()
    }

    override fun detachView() {
        this.view = null
    }

    override fun onDestroy() {
        disposer.dispose()
        interactor.clearReferences()
    }

    /**
     * Загрузить сообщение
     */
    private fun loadMessage() {
        interactor.getMessage(messageInfo.messageUuid)
            .subscribe(::handleMessageLoadingResult)
            .storeIn(disposer)
    }

    /**
     * Обработать результат загрузки модели сообщения
     * @param message модель сообщения
     */
    private fun handleMessageLoadingResult(message: ConversationMessage) {
        this.message = message
        displayViewState()
    }

    /**
     * Отобразить состояние вью
     */
    private fun displayViewState() {
        message?.let {
            view?.showMessage(it)
        }
    }
}