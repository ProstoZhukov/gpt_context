package ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.dialog_selection.data.DialogsFilter
import ru.tensor.sbis.communicator.dialog_selection.presentation.DIALOG_LIST_SIZE
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*
import javax.inject.Inject

/**
 * Результат контроллера диалогов
 */
internal typealias DialogsResult = List<ConversationModel>

/**
 * Реализация обертки контроллера диалогов для экрана выбора диалога/участников
 * @property controller провайдер контроллера диалогов [ThemeController]
 *
 * @author vv.chekurda
 */
internal class ThemeServiceWrapperImpl @Inject constructor(
    private val controller: DependencyProvider<ThemeController>,
    private val conversationMapper: ConversationMapper
) : ThemeServiceWrapper {

    @Volatile
    private var searchRequestId: UUID? = null
    @Volatile
    private var currentDataSize: Int = 0
    @Volatile
    private var searchIsActive: Boolean = false
    @Volatile
    private var subscription: Subscription? = null

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any =
        controller.get().dataRefreshed().subscribe(object : DataRefreshedThemeControllerCallback() {
            override fun onEvent(params: HashMap<String, String>) {
                val isValidRequestId = params.containsKey(SEARCH_REQUEST_ID_KEY)
                    && params[SEARCH_REQUEST_ID_KEY] == searchRequestId.toString()
                    && params[REGISTRY_KEY] == TAIL_ADDED_VALUE
                val isMaxDialogs = currentDataSize == DIALOG_LIST_SIZE

                if (!searchIsActive || searchIsActive && !isMaxDialogs && isValidRequestId) {
                    callback(params)
                }
            }
        }).also {
            subscription?.disable()
            subscription = it
        }

    override fun list(filter: DialogsFilter): DialogsResult =
        if (filter.needSync) {
            currentDataSize = 0
            searchIsActive = filter.searchString.isNotBlank()
            val result = controller.get()
                .list(filter)
                .also { searchRequestId = it.metadata?.get(SEARCH_REQUEST_ID_KEY)?.let(UUID::fromString) }
                .result
                .map(conversationMapper::apply)

            if (searchIsActive && result.isEmpty()) {
                getFirstSearchCacheCollection(filter)
            } else {
                result
            }
        }
        else {
            refresh(filter)
        }

    /**
     * Получить первую выборку диалогов из кэша по поисковому запросу [filter].
     *
     * Во время поиска в момент вызова list - контроллер ничего не ищет и не возвращает, только отдает requestId
     * и запускает асинхронный активный поиск результатов по кэшу и облаку по новому поисковому запросу.
     * Задержка необходима, чтобы дать немного времени на подготовку первичной выборки из кэша.
     * В ином случае возвращение пустого результата вызова list приведет к перемаргиванию списка на экране
     * из-за анимации и DiffUtils.
     *
     * В реестре диалогов данная проблема решается специальным костылем в AbstractTwoWayPaginationPresenter,
     * который во время поиска в течение одной секунды не подставляет пустой результат на случай,
     * если в течение этого времени сработает колбэк и спровоцирует рефреш, по которому придут данные по запросу.
     * В SbisList данная механика не поддержана, и кажется избыточной во внедрении в сам компонент,
     * тк это прикладные особенности.
     */
    private fun getFirstSearchCacheCollection(filter: DialogsFilter): DialogsResult {
        Thread.sleep(EMPIRE_CACHE_COLLECTION_WAITING_MS)
        return refresh(filter)
    }

    override fun refresh(filter: DialogsFilter): DialogsResult =
        controller.get()
            .refresh(
                filter.apply {
                    needSync = false
                }
            )
            .result
            .map(conversationMapper::apply)
            .also { currentDataSize = it.size }
}

private const val SEARCH_REQUEST_ID_KEY = "search_request_id"
private const val REGISTRY_KEY = "registry"
private const val TAIL_ADDED_VALUE = "tail_added"
private const val EMPIRE_CACHE_COLLECTION_WAITING_MS = 100L
