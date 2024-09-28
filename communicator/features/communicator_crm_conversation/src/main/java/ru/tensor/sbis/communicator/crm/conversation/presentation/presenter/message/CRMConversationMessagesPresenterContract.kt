package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.adapter.CRMMessageActionsListener
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.CRMActionButtonsClickListener

/**
 * Контракт делегата презентера по секции сообщений чата.
 */
internal interface CRMConversationMessagesPresenterContract<VIEW : CRMConversationMessagesView>
    : BaseConversationMessagesPresenterContract<VIEW>,
    CRMMessageActionsListener,
    CRMActionButtonsClickListener {

    /**@SelfDocumented*/
    fun openNewConsultation()

    /**
     * Открыть шторку.
     */
    fun openHistoryView()

    /**
     * Шторка истории консультации закрыта.
     */
    fun onHistoryViewClosed()

    /**
     * Получить список приветствий.
     */
    suspend fun getGreetings()

    /**
     * Завершена ли консультация.
     */
    val isConsultationCompleted: Boolean

    /**
     * Добавить приветственные кнопки в список сообщений.
     * Используется, чтобы отобразить их частью списка, а не отдельной вьюхой.
     */
    fun insertGreetingsButtonsInMessageList(withNotify: Boolean = false)

    /**
     * Открыть панель быстрых ответов по нажатию на кнопку панели сообщений.
     */
    fun showQuickReplyView()

    /**
     * Скрыть панель быстрых ответов, вызванную по нажатию на кнопку панели сообщений.
     */
    fun hideQuickReplyView()
}