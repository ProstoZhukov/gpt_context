package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListUpdateActions
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl
import javax.inject.Inject

/**
 * Реализация фабрики вью-модели секции списка статусов прочитанности сообщения
 * @see ReadStatusListScreenVMImpl
 *
 * @property listViewModel вью-модель списка
 * @property liveData      параметры состояния
 * @property updateActions действия для вызовов обновлениея списка
 * @property networkUtils  утилиты для работы с состоянием сети
 *
 * @author vv.chekurda
 */
internal class ReadStatusListScreenVMFactory @Inject constructor(
    private val listViewModel: ListScreenVMImpl<ReadStatusScreenEntity>,
    private val liveData: ReadStatusListVMLiveData,
    private val updateActions: ReadStatusListUpdateActions,
    private val networkUtils: NetworkUtils
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ReadStatusListScreenVMImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return ReadStatusListScreenVMImpl(
            listViewModel,
            liveData,
            updateActions,
            networkUtils
        ) as T
    }
}