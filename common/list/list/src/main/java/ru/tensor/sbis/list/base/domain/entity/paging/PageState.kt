package ru.tensor.sbis.list.base.domain.entity.paging

/**
 * Состояния загрузки страниц в [PagingData]
 *
 * @author ma.kolpakov
 */
internal enum class PageState {

    /**
     * Страница впервые загружена. Например, из кэша или после полной очистки данных.
     *
     * @see [CLEARED]
     */
    LOADED,

    /**
     * Страница была актуализирована (была загружена ранее и перезаписана обновлением)
     */
    UPDATED,

    /**
     * Страница была вытеснена из-за ограничения [PagingData._maxPages]
     */
    CROPPED,

    /**
     * Страница загружалась, но была удалена при полной очистке данных
     *
     * @see [PagingData.clear]
     */
    CLEARED,

    /**
     * Страница была удалена
     * @see [PagingData.removePage]
     */
    DELETED
}