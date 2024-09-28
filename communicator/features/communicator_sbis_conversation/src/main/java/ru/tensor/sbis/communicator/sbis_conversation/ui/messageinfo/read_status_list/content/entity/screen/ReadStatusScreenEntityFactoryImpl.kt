package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen

import ru.tensor.sbis.communicator.generated.ListResultOfMessageReceiverReadStatusMapOfStringString
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusFilterAndPageProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListPagingEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusScreenEntityFactory
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import javax.inject.Inject

/**
 * Реализация фабрики для создания бизнес модели экрана списка статусов прочитанности сообщения
 * @see [EntityFactory]
 * @see [ReadStatusScreenEntity]
 *
 * @property pagingEntity  бизнес модель для обработки данных страницы [PagingEntity]
 * @property filterCreator создатель фильтров для запросов
 *
 * @author vv.chekurda
 */
internal class ReadStatusScreenEntityFactoryImpl @Inject constructor(
    private val pagingEntity: ReadStatusListPagingEntity,
    private val filterCreator: ReadStatusFilterAndPageProvider
) : ReadStatusScreenEntityFactory {

    override fun createEntity(): ReadStatusScreenEntity =
        ReadStatusScreenEntityImpl(pagingEntity, filterCreator)

    override fun updateEntityWithData(
        page: Int,
        entity: ReadStatusScreenEntity,
        serviceResult: ListResultOfMessageReceiverReadStatusMapOfStringString
    ) {
        entity.update(page, serviceResult)
    }
}