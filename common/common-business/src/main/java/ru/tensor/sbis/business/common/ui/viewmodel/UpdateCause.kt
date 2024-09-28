package ru.tensor.sbis.business.common.ui.viewmodel

/**
 * Набор причин триггеров обращения к интерактору данных
 *
 * @param isRefreshCallback true если причина для чтения из кэша
 */
enum class UpdateCause(
    val isInitialRefresh: Boolean = false,
    val isPullToRefresh: Boolean = false,
    val isScrollToRefresh: Boolean = false,
    val isRefreshCallback: Boolean = false,
    val isRequestFromCache: Boolean = false,
    val isFilterChange: Boolean = false
) {
    /**
     * принудительно обновлять/синхронизироваться с облаком (с учетом пагинации),
     * подобно [PULL_TO_REFRESH], [SCROLL_TO_REFRESH], но не должен обрабатываться прогрессами обновления
     */
    INITIAL_REFRESH(isInitialRefresh = true),
    /**
     * свайп пользователя
     * принудительно обновлять/синхронизироваться с облаком (с учетом пагинации)
     */
    PULL_TO_REFRESH(isPullToRefresh = true),
    /**
     * скролл пользователя при пагинации
     * принудительно обновлять/синхронизироваться с облаком (с учетом пагинации)
     */
    SCROLL_TO_REFRESH(isScrollToRefresh = true),
    /**
     * получен callback о завершении синхронизации
     * подобно [REQUEST_FROM_CACHE], но используется для во избежания рекурсивного обращения
     * к репозиторию (например когда получаем ложное hasMore)
     */
    REFRESH_CALLBACK(isRefreshCallback = true),
    /**
     * чтение из кэша
     */
    REQUEST_FROM_CACHE(isRequestFromCache = true),
    /**
     * изменения в фильтре данных
     */
    FILTER_CHANGE(isFilterChange = true);
}