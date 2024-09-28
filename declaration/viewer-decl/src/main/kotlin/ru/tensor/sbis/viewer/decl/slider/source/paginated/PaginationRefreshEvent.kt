package ru.tensor.sbis.viewer.decl.slider.source.paginated

/**
 * События колбэков пагинации
 *
 * @author vv.chekurda
 */
enum class PaginationRefreshEvent {
    /**
     * Данные обновились
     */
    LIST_UPDATED,

    /**
     * Новые данные загрузились
     */
    NEWER_ADDED,

    /**
     * Старые данные загрузились
     */
    OLDER_ADDED
}