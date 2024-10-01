package ru.tensor.sbis.communicator.sbis_conversation.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.util.message_search.ThemeMessageSearchApi
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import java.util.UUID

class MessageSearchHelper {

    private lateinit var themeMessageSearchApi: ThemeMessageSearchApi

    // Активен ли режим поиска
    private val _isSearchModeActive = MutableStateFlow(false)
    val isSearchModeActive: StateFlow<Boolean> = _isSearchModeActive.asStateFlow()

    // Поисковая строка
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Выбранная персона
    private val _selectedPerson = MutableStateFlow<PersonSuggestData?>(null)
    val selectedPerson: StateFlow<PersonSuggestData?> = _selectedPerson.asStateFlow()

    // Список найденных сообщений (UUID идентификаторы) - ленивый доступ
    val foundMessageIds: Flow<List<UUID>> by lazy {
        getFoundMessageIdsFlow()
    }

    // Список предлагаемых персон для саггеста - ленивый доступ
    val suggestedPersons: Flow<List<PersonSuggestData>> by lazy {
        ensureApiInitialized()
        themeMessageSearchApi.suggestedPersons
    }

    // Индекс текущего выбранного сообщения из найденных
    private val _currentFoundMessageIndex = MutableStateFlow(-1)
    val currentFoundMessageIndex: StateFlow<Int> = _currentFoundMessageIndex.asStateFlow()

    // Событие для навигации к сообщению (UUID)
    private val _navigateToMessageIdEvent = MutableSharedFlow<UUID>()
    val navigateToMessageIdEvent: SharedFlow<UUID> = _navigateToMessageIdEvent.asSharedFlow()

    // Режим отображения списком
    private val _isListViewMode = MutableStateFlow(false)
    val isListViewMode: StateFlow<Boolean> = _isListViewMode.asStateFlow()

    // Состояния UI
    val isMessagePanelVisible: Flow<Boolean> = _isSearchModeActive.map { !it }
    val isSearchBarVisible: Flow<Boolean> = isSearchModeActive
    val isNavigationButtonsVisible: Flow<Boolean> by lazy {
        ensureApiInitialized()
        foundMessageIds.map { it.isNotEmpty() }
    }
    val isSwitchViewModeButtonVisible: Flow<Boolean> by lazy {
        ensureApiInitialized()
        isNavigationButtonsVisible
    }

    // Активация режима поиска
    fun activateSearchMode() {
        _isSearchModeActive.value = true
    }

    // Деактивация режима поиска
    fun deactivateSearchMode() {
        _isSearchModeActive.value = false
        _searchQuery.value = ""
        _selectedPerson.value = null
        _currentFoundMessageIndex.value = -1
        _isListViewMode.value = false

        // Сброс параметров поиска в API
        if (::themeMessageSearchApi.isInitialized) {
            themeMessageSearchApi.setSearchMessagesQuery("")
            themeMessageSearchApi.setSearchMessagesPerson(null)
        }
    }

    // Обработка изменения поисковой строки
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (::themeMessageSearchApi.isInitialized) {
            themeMessageSearchApi.setSearchMessagesQuery(query)
        }
    }

    // Обработка выбора персоны
    fun onPersonSelected(person: PersonSuggestData?) {
        _selectedPerson.value = person
        if (::themeMessageSearchApi.isInitialized) {
            themeMessageSearchApi.setSearchMessagesPerson(person)
        }
    }

    // Навигация к следующему найденному сообщению
    suspend fun navigateToNextFoundMessage() {
        val messageIds = foundMessageIds.firstOrNull() ?: return
        if (messageIds.isEmpty()) return

        val nextIndex = (_currentFoundMessageIndex.value + 1) % messageIds.size
        _currentFoundMessageIndex.value = nextIndex
        val messageId = messageIds[nextIndex]
        _navigateToMessageIdEvent.emit(messageId)
    }

    // Навигация к предыдущему найденному сообщению
    suspend fun navigateToPreviousFoundMessage() {
        val messageIds = foundMessageIds.firstOrNull() ?: return
        if (messageIds.isEmpty()) return

        val prevIndex = if (_currentFoundMessageIndex.value - 1 < 0) {
            messageIds.size - 1
        } else {
            _currentFoundMessageIndex.value - 1
        }
        _currentFoundMessageIndex.value = prevIndex
        val messageId = messageIds[prevIndex]
        _navigateToMessageIdEvent.emit(messageId)
    }

    // Переключение режима отображения
    fun switchViewMode() {
        _isListViewMode.value = !_isListViewMode.value
    }

    // Обработка клика по сообщению в режиме списка
    suspend fun onMessageClicked(messageId: UUID) {
        _isListViewMode.value = false
        val messageIds = foundMessageIds.firstOrNull() ?: return
        val index = messageIds.indexOf(messageId)
        if (index != -1) {
            _currentFoundMessageIndex.value = index
            _navigateToMessageIdEvent.emit(messageId)
        }
    }

    // Получение списка найденных сообщений (UUID) - ленивый доступ
    private fun getFoundMessageIdsFlow(): Flow<List<UUID>> {
        ensureApiInitialized()
        // Здесь необходимо реализовать получение списка UUID найденных сообщений
        // Предположим, что у нас есть поток найденных сообщений из другого компонента
        return themeMessageSearchApi.foundMessages
    }

    /**
     * Инициализация ThemeMessageSearchApi.
     * Должен быть вызван до использования любых методов, зависящих от API.
     */
    fun initThemeMessageSearchApi(api: ThemeMessageSearchApi) {
        themeMessageSearchApi = api
    }

    /**
     * Проверка инициализации themeMessageSearchApi.
     * Если не инициализирован, выбрасывает исключение.
     */
    private fun ensureApiInitialized() {
        if (!::themeMessageSearchApi.isInitialized) {
            throw IllegalStateException("ThemeMessageSearchApi не инициализирован. Вызовите initThemeMessageSearchApi(api) перед использованием.")
        }
    }
}

