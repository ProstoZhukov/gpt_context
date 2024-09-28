package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusFocusChangeListener
import java.util.*

/**
 * Зависимости view списка получателей прочитавших/не прочитавших сообщение.
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListViewDependency {

    /**
     * Фрагмент, в котором отображается список.
     */
    val fragment: Fragment

    /**
     * Идентификатор сообщения, по которому будет сформирован список получателей.
     */
    val messageUuid: UUID

    /**
     * Признак групповой переписки.
     */
    val isGroupConversation: Boolean

    /**
     * Роутер для навигации.
     */
    val communicatorConversationRouter: CommunicatorConversationRouter

    val focusChangeListener: ReadStatusFocusChangeListener
}