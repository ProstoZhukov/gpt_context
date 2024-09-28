package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.paging

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.stub.ReadStatusStubContentProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListMapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListPagingEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusResultHelper
import ru.tensor.sbis.list.base.domain.entity.paging.PagingData
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import javax.inject.Inject

/**
 * Фабрика [PagingEntity] списка статусов прочитанности сообщения
 *
 * @property mapper     маппер моделей микросервиса в модели компонента
 * @property helper     вспомогательнй класс для обработки результата сервиса
 * @property pagingData вспомогательный класс, содержащий загруженные страницы
 *
 * @author vv.chekurda
 */
internal class ReadStatusPagingEntityFactory @Inject constructor(
    private val mapper: ReadStatusListMapper,
    private val helper: ReadStatusResultHelper,
    private val stubContentProvider: ReadStatusStubContentProvider,
    private val pagingData: PagingData<ReadStatusListResult>
) {

    /**
     * Создать [ReadStatusListPagingEntity]
     */
    fun createPagingEntity(): ReadStatusListPagingEntity =
        PagingEntity(
            mapper = mapper,
            helper = helper,
            stubContentProvider = stubContentProvider,
            itemsOnPage = READ_STATUS_LIST_ITEMS_ON_PAGE_COUNT,
            maxPages = READ_STATUS_LIST_MAX_PAGES_COUNT,
            pagingData = pagingData
        )
}

/** Количество элементов для запроса одной страницы */
internal const val READ_STATUS_LIST_ITEMS_ON_PAGE_COUNT = 50L

/**
 * Максимальное количество страниц для пагинации.
 * Микросервис поддерживает только одностороннюю пагинацию, поэтому max
 */
internal const val READ_STATUS_LIST_MAX_PAGES_COUNT = Int.MAX_VALUE