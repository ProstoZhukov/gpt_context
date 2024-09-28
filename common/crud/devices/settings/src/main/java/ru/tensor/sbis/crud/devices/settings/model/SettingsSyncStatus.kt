package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.SyncStatus as ControllerSyncStatus

/**
 * Перечисление статусов синхронизации настроек: NOT_REQUIRED, REQUIRED, SYNCED, DELETED
 */
enum class SettingsSyncStatus {
    NOT_REQUIRED,
    REQUIRED,
    SYNCED,
    DELETED
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SettingsSyncStatus.map(): ControllerSyncStatus =
        when (this) {
            SettingsSyncStatus.NOT_REQUIRED -> ControllerSyncStatus.NOT_REQUIRED
            SettingsSyncStatus.REQUIRED -> ControllerSyncStatus.REQUIRED
            SettingsSyncStatus.SYNCED -> ControllerSyncStatus.SYNCED
            SettingsSyncStatus.DELETED -> ControllerSyncStatus.DELETED
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSyncStatus.map(): SettingsSyncStatus =
        when (this) {
            ControllerSyncStatus.NOT_REQUIRED -> SettingsSyncStatus.NOT_REQUIRED
            ControllerSyncStatus.REQUIRED -> SettingsSyncStatus.REQUIRED
            ControllerSyncStatus.SYNCED -> SettingsSyncStatus.SYNCED
            ControllerSyncStatus.DELETED -> SettingsSyncStatus.DELETED
        }