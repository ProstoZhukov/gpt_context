package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID
import ru.tensor.devices.settings.generated.JewelryUtmSettings as ControllerJewelryUtmSettings

/** Настройки УТМ. */
@Parcelize
data class JewelryUtmSettings(
    val id: Int,
    val port: Int?,
    val host: String?,
    val isPassword: Boolean,
    val password: String?,
    val uuid: UUID
) : Parcelable {

    companion object {

        /** @SelfDocumented */
        fun stub() = JewelryUtmSettings(
            id = 0,
            port = null,
            host = null,
            isPassword = false,
            password = null,
            uuid = UUID.randomUUID()
        )
    }
}

/** @SelfDocumented */
fun JewelryUtmSettings.toControllerType(): ControllerJewelryUtmSettings = ControllerJewelryUtmSettings(
    id = id,
    port = port,
    host = host,
    isPassword = isPassword,
    password = password,
    uuid = uuid
)

/** @SelfDocumented */
fun ControllerJewelryUtmSettings.toAndroidType(): JewelryUtmSettings = JewelryUtmSettings(
    id = id,
    port = port,
    host = host,
    isPassword = isPassword,
    password = password,
    uuid = uuid
)
