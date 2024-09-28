package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.UtmSettings as ControllerUtmSettings

/** Настройки УТМ. */
@Parcelize
data class UtmSettings(
    val id: Int?,
    val utm: Int?,
    val autoTransferringToShop: Int,
    val port: Int?,
    val host: String?,
    val fsRar: String?,
    val alcoType: AlcoType?
) : Parcelable

/** @SelfDocumented */
fun UtmSettings.toControllerType(): ControllerUtmSettings = ControllerUtmSettings(
    id = id,
    utm = utm,
    autoTransferringToShop = autoTransferringToShop,
    port = port,
    host = host,
    fsRar = fsRar,
    alcoType = alcoType?.map()
)

/** @SelfDocumented */
fun ControllerUtmSettings.toAndroidType(): UtmSettings = UtmSettings(
    id = id,
    utm = utm,
    autoTransferringToShop = autoTransferringToShop,
    port = port,
    host = host,
    fsRar = fsRar,
    alcoType = alcoType?.map()
)
