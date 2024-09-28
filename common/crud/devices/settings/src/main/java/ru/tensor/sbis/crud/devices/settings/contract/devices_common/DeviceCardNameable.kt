package ru.tensor.sbis.crud.devices.settings.contract.devices_common

/** Интерфейс, описывающий карточку, оборудовнаие которой может иметь имя. Не путать с моделью устройства. */
interface DeviceCardNameable {

    /** Получить текущее имя устройства. */
    fun getName(): String

    /** Установить имя устройства. */
    fun setName(name: String)
}