package ru.tensor.sbis.hallscheme.v2.business.model.tableinfo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Элемент обводки для нескольких объектов.
 */
@Parcelize
data class Outline(
    val svgPath: String,
    val width: Double,
    val height: Double,
    val x: Double,
    val y: Double
) : Parcelable