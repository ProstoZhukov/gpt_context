package ru.tensor.sbis.hallscheme.v2.business.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель, описывающая фон схемы зала.
 * @param position расположение изображения на схеме.
 * @param repeat способ замощения изображения enum": ["no-repeat", "repeat", "repeat-x", "repeat-y"].
 * @param size размер отображаемого изображения.
 * @param url ссылка на картинку с фоном.
 */
@Parcelize
data class Background(
    val position: String? = null,
    val repeat: String,
    val size: String? = null,
    val url: String? = null
) : Parcelable