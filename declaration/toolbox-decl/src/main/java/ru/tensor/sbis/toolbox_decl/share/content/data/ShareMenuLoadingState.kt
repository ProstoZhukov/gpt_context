package ru.tensor.sbis.toolbox_decl.share.content.data

/**
 * Состояние загрузки контента "поделиться" для индикации отправки пользователю.
 *
 * @author vv.chekurda
 */
sealed interface ShareMenuLoadingState {

    /**
     * Начальное состояние, ничего не загружается.
     */
    object None : ShareMenuLoadingState

    /**
     * Состояние процесса загрузки.
     */
    object Loading : ShareMenuLoadingState

    /**
     * Состояние полной готовности, когда контент "поделиться" отправлен.
     */
    object Done : ShareMenuLoadingState
}