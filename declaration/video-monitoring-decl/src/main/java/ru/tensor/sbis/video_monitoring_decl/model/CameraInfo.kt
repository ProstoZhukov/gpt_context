package ru.tensor.sbis.video_monitoring_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Сводная инфомация о камере видеонаблюдения.
 * Передаётся в модуль видеомониторинга для загрузки видеопотока.
 *
 * @param id идентификатор записи оборудования
 * @param uuid uuid камеры
 * @param name название камеры
 * @param startTimestamp начальный таймстамп видео
 */
@Parcelize
data class CameraInfo(
    val id: Long,
    val uuid: UUID? = null,
    val name: String = "",
    val startTimestamp: Long? = null
) : Parcelable