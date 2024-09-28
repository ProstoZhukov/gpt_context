package ru.tensor.sbis.crud.devices.settings.contract.devices_common

import java.util.UUID

/** Интерфейс, описывающий карточку, умеющую предоставлять информацию о рабочем месте, на котором она создана/создаётся. */
interface DeviceCardWithWorkplace {

    /** Получить рабочее место, которое ассоциируется с карточкой. */
    fun getWorkplaceID(): UUID
}