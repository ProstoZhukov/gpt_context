package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.AlcoMode as ControllerAlcoMode

/** @SelfDocumented */
@Parcelize
data class AlcoMode(val contractor: Int, val alcoType: AlcoType?) : Parcelable

/** @SelfDocumented */
fun ControllerAlcoMode.map() = AlcoMode(contractor, alcoType?.map())

/** @SelfDocumented */
fun AlcoMode.map() = ControllerAlcoMode(contractor, alcoType?.map())