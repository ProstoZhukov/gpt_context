package ru.tensor.sbis.viewer.decl.slider.source.paginated

/**
 * Направления обновления списка паджинируемого просмотрщика вложений
 *
 * @author vv.chekurda
 */
enum class ViewerUpdatingDirection {
    /**
     * Загрузка и синхронизация первой страницы
     */
    FIRST_PAGE,

    /**
     * Загрузка и синхронизация новой страницы
     */
    LOAD_NEXT,

    /**
     * Загрузка и синхронизация старой страницы
     */
    LOAD_PREVIOUS,

    /**
     * Обновление из кэша текущего списка
     */
    UPDATE,

    /**
     * Обновление из кэша новой страницы
     */
    REFRESH_NEWER,

    /**
     * Обновление из кэша старой страницы
     */
    REFRESH_OLDER
}