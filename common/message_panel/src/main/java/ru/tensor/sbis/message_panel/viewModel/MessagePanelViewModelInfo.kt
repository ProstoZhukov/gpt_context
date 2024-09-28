package ru.tensor.sbis.message_panel.viewModel

import ru.tensor.sbis.message_panel.model.CoreConversationInfo

/**
 * Информация о параметрах работы [MessagePanelViewModel], которая получается на основе [CoreConversationInfo]
 *
 * @author vv.chekurda
 * Создан 8/12/2019
 */
interface MessagePanelViewModelInfo {

    /**
     * Актуальные параметры работы панели ввода
     */
    val conversationInfo: CoreConversationInfo
}