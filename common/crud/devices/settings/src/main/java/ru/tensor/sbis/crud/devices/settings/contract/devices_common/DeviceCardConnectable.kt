package ru.tensor.sbis.crud.devices.settings.contract.devices_common

import ru.tensor.sbis.crud.devices.settings.model.DeviceConnectionInside

/** Интерфейс, описывающий карточку, имеющую соединение. */
interface DeviceCardConnectable {

    /** Получить текущее соединение карточки. */
    fun getConnection(): DeviceConnectionInside

    /** Установить соединение карточки. */
    fun setConnection(connection: DeviceConnectionInside)
}