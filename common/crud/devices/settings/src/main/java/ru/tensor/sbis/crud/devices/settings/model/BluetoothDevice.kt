package ru.tensor.sbis.crud.devices.settings.model

/**
 * Модель, описывающая bluetooth-устройство.
 *
 * @param name - название устройства.
 * @param macAddress - MAC-адрес устройства.
 * */
data class BluetoothDevice(val name: String, val macAddress: String)