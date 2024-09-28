package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.isNetworkError
import ru.tensor.sbis.list.base.domain.entity.paging.PagingData
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl
import javax.inject.Inject

/**
 * Делегат для обработки вызовов обновления списка статусов прочитанности сообщения
 * @see [ReadStatusListUpdateActions]
 *
 * @property interactor    интерактор списка статусов прочитанности
 * @property screenEntity  бизнес модель экрана списка
 * @property listVM        вью-модель компонента списка
 *
 * @author vv.chekurda
 */
internal class ReadStatusListActionsDelegate @Inject constructor(
    private val interactor: ReadStatusListInteractor,
    private val screenEntity: ReadStatusScreenEntity,
    private val listVM: ListScreenVMImpl<ReadStatusScreenEntity>,
    private val pagingData: PagingData<ReadStatusListResult>
) : ReadStatusListUpdateActions {

    override fun reloadList() {
        screenEntity.cleanPagesData()
        listVM.listData.value = screenEntity.toListData()
        interactor.firstPage(screenEntity, listVM)
    }

    override fun refreshList() {
        interactor.refresh(screenEntity, listVM)
    }

    override fun onNetworkConnected() {
        val lastPageData = pagingData.lastPageData()
        val lastPageKey = pagingData.lastKeyOrZeroIfEmpty()
        // Удаление последнего результата с элементом ошибки загрузки страницы
        if (lastPageData?.metadata?.isNetworkError == true) {
            pagingData.removePage(lastPageKey)
        }
        // Попытка загрузки новой страницы, если результат последней haveMore или вовсе список пуст
        if (pagingData.lastPageData()?.haveMore == true || pagingData.isEmpty()) {
            interactor.nextPage(screenEntity, listVM)
        }
    }
}