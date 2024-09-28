package ru.tensor.sbis.communicator.base.conversation.presentation.adapter

/**
 * Обработчик действий показа ошибок пагинации.
 *
 * @author vv.chekurda
 */
interface PagingLoadingErrorActions {
    /**
     * Показать ошибку загрузки новой страницы.
     */
    fun showNewerLoadingError()

    /**
     * Показать ошибку загрузки старой страницы.
     */
    fun showOlderLoadingError()

    /**
     * Сбросить ошибки пагинации.
     */
    fun resetPagingLoadingError()
}