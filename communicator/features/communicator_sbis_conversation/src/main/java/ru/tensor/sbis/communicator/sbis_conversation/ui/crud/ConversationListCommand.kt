package ru.tensor.sbis.communicator.sbis_conversation.ui.crud

import io.reactivex.Observable
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationPrefetchManagerImpl.Companion.INITIAL_LOADING_STRING_ID
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand

/** @SelfDocumented */
internal class ConversationListCommand(
    conversationRepository: ConversationRepository,
    private val themeRepository: ThemeRepository,
    private val mapper: BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<ConversationMessage>>,
    private val prefetchManager: ConversationPrefetchManagerImpl? = null
) : BaseListCommand<ConversationMessage, ListResultOfMessageMapOfStringString, MessageFilter, DataRefreshedMessageControllerCallback>(
    conversationRepository,
    mapper
) {
    var onThemeAfterOpenedCallback: (MessageFilter) -> Unit = {}

    override fun refresh(filter: MessageFilter): Observable<PagedListResult<ConversationMessage>> =
        // Сначала пытаемся получить предзагруженные данные из ConversationPrefetchManager, иначе получаем данные напрямую
        if (filter.requestId == INITIAL_LOADING_STRING_ID) {
            // При входе в переписку initialRefresh был вызван ранее из prefetchManager или делаем это напрямую.
            val observable = prefetchManager?.prefetchListCommand(filter) ?: prefetch(filter)
            // После окончания инициализирущей загрузки должен быть вызван onThemeAfterOpened с тем же фильтром
            // для запуска всех синхронизаций по переписке
            observable.doAfterTerminate { onThemeAfterOpenedCallback(filter) }
        } else super.refresh(filter)

    fun prefetch(filter: MessageFilter): Observable<PagedListResult<ConversationMessage>> =
        Observable.fromCallable {
            themeRepository.onThemeBeforeOpened(filter.themeId)
            val result = mRepository.refresh(filter)
            mapper.apply(result)
        }
}