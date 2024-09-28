package ru.tensor.sbis.video_monitoring_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель помещения с камерами.
 *
 * @param id идентификатор помещения
 * @param name название помещения
 */
@Parcelize
data class CameraRoom(
    val id: Int,
    val name: String
) : Parcelable