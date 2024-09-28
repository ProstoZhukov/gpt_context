package ru.tensor.sbis.crud.devices.settings.contract.devices_common

import ru.tensor.sbis.crud.devices.settings.model.DriverInfo

/** Интерфейс, описывающий карточку, имеющую драйвера. */
interface DeviceCardWithDrivers {

    /** Получить список поддерживаемых драйверов. */
    fun getDrivers(): List<DriverInfo>
}