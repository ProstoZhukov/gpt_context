package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.RemoteWorkplaceInfo as ControllerRemoteWorkplaceInfo

/**@SelfDocumented */
@Parcelize
data class RemoteWorkplaceInfo(val id: Long, val name: String, val printReceipt: Boolean) : Parcelable

/**@SelfDocumented */
fun ControllerRemoteWorkplaceInfo.toAndroidType() = RemoteWorkplaceInfo(id, name, printReceipt)

/**@SelfDocumented */
fun RemoteWorkplaceInfo.toControllerType() = ControllerRemoteWorkplaceInfo(id, name, printReceipt)