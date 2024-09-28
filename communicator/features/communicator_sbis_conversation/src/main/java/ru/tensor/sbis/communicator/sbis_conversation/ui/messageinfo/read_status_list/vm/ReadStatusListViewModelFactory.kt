package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusLifeCycleHolder
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list.ReadStatusListScreenVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVM
import javax.inject.Inject

/**
 * Реализация фабрики вью-модели списка статусов прочитанности сообщения
 * @see [ReadStatusListViewModelImpl]
 *
 * @property searchInputViewModel вью-модель поисковой строки
 * @property listViewModel        вью-модель списка
 * @property liveData             параметры состояния
 * @property lifeCycleHolder      обертка для управления жизненным циклом
 *
 * @author vv.chekurda
 */
internal class ReadStatusListViewModelFactory @Inject constructor(
    private val searchInputViewModel: ReadStatusSearchInputVM,
    private val listViewModel: ReadStatusListScreenVM,
    private val liveData: ReadStatusListVMLiveData,
    private val lifeCycleHolder: ReadStatusLifeCycleHolder
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ReadStatusListViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return ReadStatusListViewModelImpl(
            searchInputViewModel,
            listViewModel,
            liveData,
            lifeCycleHolder
        ) as T
    }
}