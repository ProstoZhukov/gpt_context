package ru.tensor.sbis.crud.devices.settings.model

/**
 * Перечисление статусов синхронизации: NOT_REQUIRED, REQUIRED, SYNCED, DELETED
 */
enum class SyncStatusInside {
    /** Синхронизация не требуется.  */
    NOT_REQUIRED,
    /** Синхронизация требуется.  */
    REQUIRED,
    /** Синхронизация выполнена.  */
    SYNCED,
    /** Удалено локально, требуется удалить в облаке.  */
    DELETED
}