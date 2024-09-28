package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract

import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusListFilterSelector
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusMessageReceiversListener

/**
 * Интерфейс view списка статусов прочитанности сообщения
 * @see [ReadStatusListFilterSelector]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListViewContract :
    ReadStatusListFilterSelector,
    ReadStatusMessageReceiversListener,
    AdjustResizeHelper.KeyboardEventListener {

    /**
     * Инициализация view
     * @param dependency зависимости, необходимые для инициализации списка
     */
    fun initReadStatusListView(dependency: ReadStatusListViewDependency)

    /**
     * Закрыть клавиатуру
     */
    fun hideKeyboard()
}