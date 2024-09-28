package ru.tensor.sbis.crud.sbis.pricing.model

import ru.tensor.sbis.library.generated.LocalStatus as ControllerLocalStatus

/**
 * Перечисление типов локальных статусов: LS_CREATED, LS_SYNCHRONIZED, LS_MODIFIED, LS_DELETED, LS_READY_FOR_SYNC
 */
enum class LocalStatus {

    LS_CREATED,
    LS_SYNCHRONIZED,
    LS_MODIFIED,
    LS_DELETED,
    LS_READY_FOR_SYNC
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerLocalStatus.map(): LocalStatus =
        when (this) {
            ControllerLocalStatus.LS_CREATED -> LocalStatus.LS_CREATED
            ControllerLocalStatus.LS_SYNCHRONIZED -> LocalStatus.LS_SYNCHRONIZED
            ControllerLocalStatus.LS_MODIFIED -> LocalStatus.LS_MODIFIED
            ControllerLocalStatus.LS_DELETED -> LocalStatus.LS_DELETED
            ControllerLocalStatus.LS_READY_FOR_SYNC -> LocalStatus.LS_READY_FOR_SYNC
        }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun LocalStatus.map(): ControllerLocalStatus =
        when (this) {
            LocalStatus.LS_CREATED -> ControllerLocalStatus.LS_CREATED
            LocalStatus.LS_SYNCHRONIZED -> ControllerLocalStatus.LS_SYNCHRONIZED
            LocalStatus.LS_MODIFIED -> ControllerLocalStatus.LS_MODIFIED
            LocalStatus.LS_DELETED -> ControllerLocalStatus.LS_DELETED
            LocalStatus.LS_READY_FOR_SYNC -> ControllerLocalStatus.LS_READY_FOR_SYNC
        }