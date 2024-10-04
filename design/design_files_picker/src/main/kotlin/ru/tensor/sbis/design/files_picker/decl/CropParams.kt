package ru.tensor.sbis.design.files_picker.decl

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize

/**
 * Параметры обрезки изображения.
 *
 * @property shape Параметры обрезки изображения.
 * @property aspectRatio Параметры обрезки изображения.
 * @property minSizeLimit Параметры обрезки изображения.
 *
 * @author ia.nikitin
 */
@Parcelize
data class CropParams(
    val shape: CropShape = CropShape.RECTANGLE,
    val aspectRatio: CropAspectRatio? = null,
    val minSizeLimit: CropMinSizeLimit? = null
) : Parcelable

/**
 * Соотношение сторон.
 *
 * @author ia.nikitin
 */
@Parcelize
data class CropAspectRatio(@IntRange(from = 1) val width: Int, @IntRange(from = 1) val height: Int) : Parcelable

/**
 * Минимальный размер изображения в результате обрезки [minWidth] и [minHeight].
 *
 * @author ia.nikitin
 */
@Parcelize
data class CropMinSizeLimit(@IntRange(from = 1) val minWidth: Int, @IntRange(from = 1) val minHeight: Int) : Parcelable

/**
 * Фигура для выделения участка изображения при обрезке.
 *
 * @author ia.nikitin
 */
enum class CropShape {
    RECTANGLE,
    OVAL
}