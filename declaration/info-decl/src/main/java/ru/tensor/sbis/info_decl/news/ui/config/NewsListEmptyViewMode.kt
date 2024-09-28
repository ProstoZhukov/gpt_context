package ru.tensor.sbis.info_decl.news.ui.config

/**
 * Режимы отображения заглушек в реестре новостей
 *
 * @author am.boldinov
 */
enum class NewsListEmptyViewMode {

    /**
     * Список новостей пуст при пустой строке поиска и без установленных параметров фильтрации
     */
    JUST_EMPTY,
    /**
     * Список пуст в результате выполнения поискового запроса
     */
    EMPTY_BY_SEARCH_REQUEST,
    /**
     * Список новостей пуст в результате применения параметров фильтрации
     */
    EMPTY_CAUSE_FILTER,
    /**
     * Отсутствует интернет-соединение
     */
    CHECK_NETWORK_CONNECTION,
    /**
     * Произошла ошибка загрузки данных
     */
    DEFAULT_ERROR;

}