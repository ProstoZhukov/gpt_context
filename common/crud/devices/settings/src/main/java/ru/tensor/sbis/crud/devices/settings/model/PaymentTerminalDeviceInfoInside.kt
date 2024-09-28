package ru.tensor.sbis.crud.devices.settings.model

import java.util.*

/**
 * Информация о платежном терминале
 *
 * @param deviceInfoBaseInside Базовая информация об устройстве
 * @param terminalId Уникальный идентификатор терминала
 * @param receiptBody Образ чека для печати
 * @param organizationIdList Множество идентификаторов организации терминала
 * */
data class PaymentTerminalDeviceInfoInside(
    val deviceInfoBaseInside: DeviceInfoBaseInside,
    val terminalId: String?,
    val receiptBody: String?,
    val organizationIdList: HashSet<String>
)