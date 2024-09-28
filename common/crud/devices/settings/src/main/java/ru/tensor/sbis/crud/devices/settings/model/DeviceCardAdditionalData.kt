package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.retail_presto.g.PrinterCardStaticData
import java.util.UUID

/** Класс, описывающий статические данные, сопровождающие карточку оборудования. */
data class DeviceCardAdditionalData(
    val defaultTCPPort: Int,
    val defaultIP: String,
    val deviceKind: DeviceKindInside,
    val deviceType: UUID
)

/**@SelfDocumented*/
fun PrinterCardStaticData.toDeviceCardAdditionalData() = DeviceCardAdditionalData(
    defaultPort,
    defaultIp,
    kindToAndroidType(kind),
    type
)