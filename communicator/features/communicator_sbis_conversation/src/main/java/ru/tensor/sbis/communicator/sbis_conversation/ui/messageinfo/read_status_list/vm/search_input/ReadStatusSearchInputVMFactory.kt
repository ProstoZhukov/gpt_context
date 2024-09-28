package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListUpdateActions
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import javax.inject.Inject

/**
 * Реализация фабрики вью-модели поисковой строки списка статусов прочитанности сообщения
 * @see [ReadStatusSearchInputVMImpl]
 *
 * @param liveData      параметры состояния
 * @param updateActions действия для вызовов обновлениея списка
 *
 * @author vv.chekurda
 */
internal class ReadStatusSearchInputVMFactory @Inject constructor(
    private val liveData: ReadStatusListVMLiveData,
    private val updateActions: ReadStatusListUpdateActions
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ReadStatusSearchInputVMImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return ReadStatusSearchInputVMImpl(
            liveData,
            updateActions
        ) as T
    }
}