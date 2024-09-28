package ru.tensor.sbis.crud.devices.settings.model

import java.util.*

/**
 * Модель, описываюшая usb-устройство, подключённое к оборудованию.
 *
 * @param id Уникальный идентификатор устройства.
 * @param usbId Модель, описывающая идентификатор USB устройства (VID/PID).
 * @param devicePath Путь к устройству.
 * */
data class UsbDevice(val id: UUID, val usbId: UsbId, val devicePath: String)