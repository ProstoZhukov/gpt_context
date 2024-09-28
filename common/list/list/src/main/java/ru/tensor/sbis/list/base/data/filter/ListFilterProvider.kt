package ru.tensor.sbis.list.base.data.filter

/**
 * Содержит фильтр для микросервиса контроллера и номер страницы, в которую будет добавлен результат выборки.
 * @param FILTER тип фильтра микросервиса контроллера, класс пакета "*.generated"
 */
interface FilterAndPageProvider<FILTER> {

    /**
     * Получить фильтр для микросервиса контроллера.
     */
    fun getServiceFilter(): FILTER

    /**
     * Получить номер страницы, для которой запрашиваются данные.
     */
    fun getPageNumber(): Int
}