package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation

import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusListFilterSelector

/**
 * Интерфейс view экрана информации о сообщении
 * @see [ReadStatusListFilterSelector]
 *
 * @author vv.chekurda
 */
internal interface MessageInformationView : ReadStatusListFilterSelector {

    /**
     * Отобразить сообщение
     *
     * @param message     модель сообщения
     */
    fun showMessage(message: ConversationMessage)
}

/**
 * Интерфейс презентера экрана информации о сообщении
 */
internal interface MessageInformationPresenter : BasePresenter<MessageInformationView>