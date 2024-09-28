/**
 * Утилиты для view списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common_views.date.DateHeaderView
import ru.tensor.sbis.communicator.generated.AnchorReadStatus
import ru.tensor.sbis.communicator.generated.ListResultOfMessageReceiverReadStatusMapOfStringString
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.list.base.data.CrudRepository
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.Mapper
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import java.util.*
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Блок typealias, чтобы не утонуть в дженериках компонента списка
 */
internal typealias ReadStatusListResult = ListResultOfMessageReceiverReadStatusMapOfStringString
internal typealias ReadStatusScreenEntityFactory = EntityFactory<ReadStatusScreenEntity, ReadStatusListResult>
internal typealias ReadStatusListPagingEntity = PagingEntity<AnchorReadStatus, ReadStatusListResult, MessageReceiverReadStatusFilter>
internal typealias ReadStatusFilterAndPageProvider = FilterProvider<MessageReceiverReadStatusFilter>
internal typealias ReadStatusResultHelper = ResultHelper<AnchorReadStatus, ReadStatusListResult>
internal typealias ReadStatusListMapper = Mapper<ReadStatusListResult>
internal typealias ReadStatusListInteractor = ListInteractor<ReadStatusScreenEntity>
internal typealias ReadStatusListRepository = Repository<ReadStatusScreenEntity, MessageReceiverReadStatusFilter>
internal typealias ReadStatusListRepositoryImpl = CrudRepository<ReadStatusListResult, ReadStatusScreenEntity, MessageReceiverReadStatusFilter>
internal typealias ReadStatusListServiceWrapper = ServiceWrapper<ReadStatusListResult, MessageReceiverReadStatusFilter>

internal val META_IS_FIRST_PAGE = "is_first_page" to "true"
internal val META_IS_SEARCH_RESULT = "is_search_result" to "true"
private const val META_ERROR_KEY ="error"
private const val META_NETWORK_ERROR_VALUE ="network"

@BindingAdapter("date")
internal fun setDate(view: DateHeaderView, date: Date?) {
    date?.let(view::updateHeader)
}

@BindingAdapter("filterSelection")
internal fun selectFilter(view: SearchInput, filter: MessageReadStatus?) {
    filter?.let {
        val filterString =
            if (it.isDefault) {
                StringUtils.EMPTY
            } else {
                view.resources.getString(
                    getReadStatusFilterStringRes(
                        it.ordinal
                    )
                )
            }
        view.setSelectedFilters(listOf(filterString))
    }
}

/**
 * Получения ресурса названия фильтра по его порядковому номеру
 */
@StringRes
internal fun getReadStatusFilterStringRes(optionOrdinal: Int): Int =
    when (optionOrdinal) {
        MessageReadStatus.ALL.ordinal    -> RCommunicatorDesign.string.communicator_message_information_filter_all
        MessageReadStatus.READ.ordinal   -> RCommunicatorDesign.string.communicator_message_information_filter_read
        MessageReadStatus.UNREAD.ordinal -> RCommunicatorDesign.string.communicator_message_information_filter_unread
        else                             -> 0
    }

/**
 * Является ли фильтр - фильтром по умолчанию
 */
internal val MessageReadStatus.isDefault: Boolean
    get() = this == MessageReadStatus.ALL

/**
 * Для проверки метадаты контроллера на наличие ошибки сети
 */
internal val HashMap<String, String>?.isNetworkError: Boolean
    get() = this?.get(META_ERROR_KEY) == META_NETWORK_ERROR_VALUE