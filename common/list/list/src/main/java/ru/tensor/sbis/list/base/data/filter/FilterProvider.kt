package ru.tensor.sbis.list.base.data.filter

/**
 * Поставщик фильтра для микросервиса контроллера, используется при описании бизнес модели(БМ) экрана списка.
 * На основе текущего состояния БМ, строятся фильтры для получения новой порции данных в нужном направлении
 * - следующие или предшествующие относительно уже подгруженных ранее.
 *
 * @param FILTER тип фильтра микросервиса контроллера, класс пакета "*.generated"
 */
interface FilterProvider<FILTER> {

    /**
     * Получить фильтр, при котором микросервис контроллера вернет данные для следующей страницы относительно текущего
     * состояния.
     */
    fun provideFilterForNextPage(): FilterAndPageProvider<FILTER>

    /**
     * Тоже что и [provideFilterForNextPage], но в обратную сторону.
     */
    fun provideFilterForPreviousPage(): FilterAndPageProvider<FILTER>
}

interface PagesFiltersProvider<FILTER> {
    /**
     * Получить список поставщиков фильтов для всех страниц.
     */
    fun getPageFilters(): List<FilterAndPageProvider<FILTER>>
}