package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

import ru.tensor.sbis.communicator.sbis_conversation.databinding.CommunicatorReadStatusListViewBinding
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency

/**
 * Интерфейс вью-модели поисковой строки списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusSearchInputVM :
    ReadStatusListFilterSelector,
    ReadStatusMessageReceiversListener {

    /**
     * Инициализация вью-модели
     *
     * @param binding    binding вью списка статусов прочитанности
     * @param dependency зависимости для инициализации
     */
    fun initViewModel(
        binding: CommunicatorReadStatusListViewBinding,
        dependency: ReadStatusListViewDependency
    )
}