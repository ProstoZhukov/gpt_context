package ru.tensor.sbis.crud.devices.settings.model

import java.util.*

/**
 * Основная информация об устройстве
 *
 * @param model Модель устройства
 * @param serialNumber Серийный (заводской) номер устройства
 * @param firmwareVersion Версия прошивки устройства
 * @param dateTime Текущие дата/время устройства
 * */
data class DeviceInfoBaseInside(
    val model: String,
    val serialNumber: String?,
    val firmwareVersion: String?,
    val dateTime: Date?
)