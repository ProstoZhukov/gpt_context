package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListViewLiveData
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVM
import ru.tensor.sbis.list.base.presentation.ListScreenVM

/**
 * Интерфейс вью-модели списка статусов прочитанности сообщения
 * @see [ReadStatusListViewLiveData]
 * @see [ReadStatusSearchInputVM]
 * @see [ListScreenVM]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListViewModel :
    ReadStatusListViewLiveData,
    ReadStatusSearchInputVM,
    ListScreenVM