package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.SharedWorkplaceInfo as ControllerSharedWorkplaceInfo

/** @SelfDocumented */
@Parcelize
data class SharedWorkplaceInfo(val id: Long, val name: String) : Parcelable

/** @SelfDocumented */
fun ControllerSharedWorkplaceInfo.toAndroidType() = SharedWorkplaceInfo(id, name)

/** @SelfDocumented */
fun SharedWorkplaceInfo.toControllerType() = ControllerSharedWorkplaceInfo(id, name)