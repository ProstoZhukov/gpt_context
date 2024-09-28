package ru.tensor.sbis.business.common.domain.filter.base

/**
 * Результаты синхронизации
 */
internal enum class RefreshCallbackSyncResult(
    val isSuccess: Boolean,
    val isCompleted: Boolean
) {
    /**
     * синхронизация не завершена
     */
    INCOMPLETE(false, false),

    /**
     * синхронизация завершена успешно
     */
    SUCCESS(true, true),

    /**
     * синхронизация завершена с ошибкой
     */
    ERRORS(false, false),

    /**
     * синхронизация была пропущена (менее 30 сек с последней)
     */
    SKIPPED(false, true)
}