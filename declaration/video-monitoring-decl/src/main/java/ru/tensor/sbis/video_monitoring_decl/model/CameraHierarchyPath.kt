package ru.tensor.sbis.video_monitoring_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Иерархия принадлежности и членства камер(ы).
 *
 * @param title название помещения в хлебных крошках
 * @param path путь до камеры в хлебных крошках
 */
@Parcelize
class CameraHierarchyPath(
    val title: String = "",
    val path: List<CameraRoom> = emptyList()
) : Parcelable