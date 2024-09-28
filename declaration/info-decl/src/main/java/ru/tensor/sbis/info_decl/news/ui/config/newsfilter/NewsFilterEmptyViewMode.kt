package ru.tensor.sbis.info_decl.news.ui.config.newsfilter

/**
 * Режимы отображения заглушек в фильтре новостей
 *
 * @author s.r.golovkin
 */
enum class NewsFilterEmptyViewMode {

    /**
     * Отсутствует интернет-соединение
     */
    CHECK_NETWORK_CONNECTION,

    /**
     * Список каналов пуст при пустой строке поиска
     */
    JUST_EMPTY,

    /**
     * Список пуст в результате выполнения поискового запроса
     */
    EMPTY_BY_SEARCH_REQUEST;
}