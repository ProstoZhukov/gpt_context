package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message

import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagesView
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage

/**
 * Контракт вью для делегата секции сообщений чата CRM.
 */
internal interface CRMConversationMessagesView
    : BaseConversationMessagesView<CRMConversationMessage> {
    /**
     * Показать диалог для жалобы.
     */
    fun showComplainDialogFragment(complainUseCase: ComplainUseCase)

    /**
     * Шторку с историей консультаций.
     */
    fun showHistoryView()

    /**
     * Отправить сообщение с текстом выбранного приветствия.
     */
    fun sendGreetingMessage(text: String)

    /**
     * Показать заглушку.
     */
    fun showCantViewStub()

    /**
     * Вызвать onKeyboardOpenMeasure если высота клавиатуры больше 0, иначе onKeyboardCloseMeasure.
     */
    fun onKeyboardMeasure()
}