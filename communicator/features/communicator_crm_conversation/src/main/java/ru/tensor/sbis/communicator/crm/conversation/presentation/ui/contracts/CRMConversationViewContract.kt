package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts

import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagePanelView
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationToolbarView
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesView
import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option.CRMConversationOption

/**
 * Контракт вью чата CRM.
 *
 * @author da.zhukov
 */
internal interface CRMConversationViewContract
    : BaseConversationViewContract<CRMConversationMessage>,
    CRMConversationMessagePanelView,
    CRMConversationMessagesView,
    CRMConversationToolbarView

/**
 * Контракт вью для делегата панели сообщений чата CRM.
 */
internal interface CRMConversationMessagePanelView
    : BaseConversationMessagePanelView<CRMConversationMessage>

/**
 * Контракт вью для делегата тулбара чата CRM.
 */
internal interface CRMConversationToolbarView
    : BaseConversationToolbarView<CRMConversationMessage> {

    /**
     * Скопировать ссылку на чат.
     */
    fun copyLink(url: String)

    /**
     * Показать меню чата.
     */
    fun showCRMConversationMenu(options: List<CRMConversationOption>)

    /**
     * Закрыть фрагмент чата.
     */
    fun closeConversationFragment()

    /**
     * Показать панель-информер с ошибкой.
     */
    fun showErrorPopup(@StringRes messageRes: Int, icon: String?)
}
