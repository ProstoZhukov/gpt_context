package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm

import androidx.lifecycle.ViewModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusLifeCycleHolder
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list.ReadStatusListScreenVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListViewLiveData
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVM
import ru.tensor.sbis.list.base.presentation.ListScreenVM

/**
 * Вью-модель списка статусов прочитанности сообщения
 * @see [ReadStatusListViewModel]
 * @see [ReadStatusSearchInputVM]
 * @see [ReadStatusListViewLiveData]
 *
 * @property searchInputViewModel вью-модель поисковой строки
 * @property listViewModel        вью-модель списка
 * @property liveData             параметры состояния
 * @property lifeCycleHolder      обертка для управления жизненным циклом
 *
 * @author vv.chekurda
 */
internal class ReadStatusListViewModelImpl(
    private val searchInputViewModel: ReadStatusSearchInputVM,
    private val listViewModel: ReadStatusListScreenVM,
    private val liveData: ReadStatusListVMLiveData,
    private val lifeCycleHolder: ReadStatusLifeCycleHolder
) : ViewModel(),
    ReadStatusListViewModel,
    ReadStatusSearchInputVM by searchInputViewModel,
    ListScreenVM by listViewModel,
    ReadStatusListViewLiveData by liveData {

    override fun onCleared() {
        super.onCleared()
        lifeCycleHolder.onCleared()
    }
}