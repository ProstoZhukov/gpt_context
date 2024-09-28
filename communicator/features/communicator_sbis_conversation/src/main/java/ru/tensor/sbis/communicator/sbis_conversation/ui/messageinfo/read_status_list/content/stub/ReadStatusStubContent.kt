package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.stub

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.META_IS_SEARCH_RESULT
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase.*
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Фабрика контента заглушки для списка статусов прочитанности
 *
 * @property filter   поисковый фильтр
 * @property metaData дополнительные метаданные по запросу
 *
 * @author vv.chekurda
 */
internal class ReadStatusStubContent(
    private val filter: MessageReadStatus,
    private val metaData: HashMap<String, String>?
) : StubViewContentFactory {

    override fun invoke(context: Context) =
        when {
            isNetworkError(metaData)           -> ReadStatusStubCase.NETWORK_ERROR
            isSearchError(metaData)            -> ReadStatusStubCase.NOT_FOUND
            filter == MessageReadStatus.READ   -> ReadStatusStubCase.EMPTY_READ
            filter == MessageReadStatus.UNREAD -> ReadStatusStubCase.EMPTY_UNREAD
            else                               -> ReadStatusStubCase.NOT_FOUND
        }.createStubViewContent()
}

internal enum class ReadStatusStubCase(
    @get:StringRes val titleRes: Int,
    @get:StringRes val descriptionRes: Int = ID_NULL
){
    NETWORK_ERROR(
        titleRes = NO_CONNECTION.messageRes,
        descriptionRes = NO_CONNECTION.detailsRes
    ),

    NOT_FOUND(
        titleRes = NO_SEARCH_RESULTS.messageRes,
        descriptionRes = NO_SEARCH_RESULTS.detailsRes
    ),

    EMPTY_READ(
        titleRes = RCommunicatorDesign.string.communicator_message_information_empty_filter_read
    ),

    EMPTY_UNREAD(
        titleRes = RCommunicatorDesign.string.communicator_message_information_empty_filter_unread
    );

    fun createStubViewContent(): StubViewContent =
        ResourceImageStubContent(
            messageRes = titleRes,
            detailsRes = descriptionRes
        )
}

/**
 * Проверка на ошибку сети
 */
private fun isNetworkError(metaData: HashMap<String, String>?): Boolean =
    metaData?.get(META_NETWORK_ERROR.first) == META_NETWORK_ERROR.second

/**
 * Проверка на ошибку при поиске
 */
private fun isSearchError(metaData: HashMap<String, String>?): Boolean =
    metaData?.get(META_IS_SEARCH_RESULT.first) == META_IS_SEARCH_RESULT.second

private val META_NETWORK_ERROR = "error" to "network"