package ru.tensor.sbis.crud.sale.model

import ru.tensor.sbis.sale.mobile.generated.SyncStatus as ControllerSyncStatus

/**
 * Перечисление типов статусов синхронизации
 */
enum class SaleSyncStatus {

    /**@SelfDocumented */
    NOT_REQUIRED,

    /**@SelfDocumented */
    REQUIRED,

    /**@SelfDocumented */
    SYNCED,

    /**@SelfDocumented */
    DELETED
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SaleSyncStatus.map(): ControllerSyncStatus =
        when (this) {
            SaleSyncStatus.NOT_REQUIRED -> ControllerSyncStatus.NOT_REQUIRED
            SaleSyncStatus.REQUIRED -> ControllerSyncStatus.REQUIRED
            SaleSyncStatus.SYNCED -> ControllerSyncStatus.SYNCED
            SaleSyncStatus.DELETED -> ControllerSyncStatus.DELETED
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSyncStatus.map(): SaleSyncStatus =
        when (this) {
            ControllerSyncStatus.NOT_REQUIRED -> SaleSyncStatus.NOT_REQUIRED
            ControllerSyncStatus.REQUIRED -> SaleSyncStatus.REQUIRED
            ControllerSyncStatus.SYNCED -> SaleSyncStatus.SYNCED
            ControllerSyncStatus.DELETED -> SaleSyncStatus.DELETED
        }