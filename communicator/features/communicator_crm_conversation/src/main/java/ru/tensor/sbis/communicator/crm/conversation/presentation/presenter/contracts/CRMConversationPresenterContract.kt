package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts

import android.os.Bundle
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationMessagePanelView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationToolbarView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option.CRMConversationOption

/**
 * Контракт делегата презентера по панели сообщений чата.
 */
internal interface CRMConversationMessagePanelPresenterContract<VIEW : CRMConversationMessagePanelView>
    : BaseConversationMessagePanelPresenterContract<VIEW> {

    /**
     * Добавить к тексту в панели сообщений переданный [text].
     */
    fun pasteTextInMessagePanel(text: String)

    /**
     * Заменить текст в панели сообщений на переданный [text].
     */
    fun replaceTextInMessagePanel(text: String)

    /**
     * Отправить сообщение с текстом выбранного приветствия.
     */
    fun sendGreetingMessage(text: String)
}

/**
 * Контракт делегата презентера по тулбару чата.
 */
internal interface CRMConversationToolbarPresenterContract<VIEW : CRMConversationToolbarView>
    : BaseConversationToolbarPresenterContract<VIEW> {

    /**
     * Открыть меню чата тех. поддержки.
     */
    fun openCRMConversationMenu()

    /**
     * Обработать выбор опции из меню.
     */
    fun onConversationOptionSelected(option: CRMConversationOption)

    /**
     * Обработать результат экрана ввода комментария, для переназначения другому оператору.
     */
    fun onReassignCommentResult(result: Bundle)
}