package ru.tensor.sbis.crud.devices.settings.contract.devices_common

import ru.tensor.sbis.crud.devices.settings.model.DeviceCardAdditionalData

/** Интерфейс, описывающий карточку, информация о которой сопровождается дополнительными статичными данными. */
interface DeviceCardWithAdditionalInfo {

    /** Получить дополнительную информацию о карточке. */
    fun getAdditionalInfo(): DeviceCardAdditionalData
}