package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель, описывающая идентификатор USB устройства (VID/PID)
 * */
@Parcelize
data class UsbId(val vId: Int = 0, val pId: Int = 0) : Parcelable