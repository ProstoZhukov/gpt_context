package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.SlipCheck

/**
 * Модель с информацией о настройках устройства
 *
 * @param cashDesk Int? - cashDesk
 * @param company Long? - идентификатор компании, к котой привязано устройство
 * @param companyName String? - имя компании, к котой привязано устройство
 * @param contractor Int? - contractor
 * @param deviceKind Int? - тип устройства
 * @param editDialog String? - editDialog
 * @param ffdVersion Int? - версия ФФД
 * @param printInternetPayment Boolean? - разрешена ли печать интернет платежей
 * @param regNumber String? - регистрационный номер
 * @param remoteConnectable Boolean? - возможно ли удаленное подклчение
 * @param remoteDeviceId Int? - идентификатор удаленного устройства
 * @param serialNumber String? - серийный номер
 * @param testDialog String? - testDialog
 */
@Parcelize
data class DeviceSettingsInside(
    val cashDesk: Int? = null,
    val company: Long? = null,
    val companyName: String? = null,
    val contractor: Int? = null,
    val deviceKind: Int? = null,
    val editDialog: String? = null,
    val ffdVersion: Int? = null,
    val printInternetPayment: Boolean? = null,
    val regNumber: String? = null,
    val remoteConnectable: Boolean? = null,
    val remoteDeviceId: Long? = null,
    val serialNumber: String? = null,
    val testDialog: String? = null,
    val printZReport: Boolean? = null,
    val slipPause: Boolean? = null,
    val slipCheck: SlipCheck? = null,
    val printKitchenOrders: Boolean?,
    val organizationsInfo: List<OrganizationInfo> = emptyList(),
    val deviceApplication: DeviceApplication? = null,
    val printTicket: Boolean? = null,
    val printingSettings: PrintingSettings
) : Parcelable