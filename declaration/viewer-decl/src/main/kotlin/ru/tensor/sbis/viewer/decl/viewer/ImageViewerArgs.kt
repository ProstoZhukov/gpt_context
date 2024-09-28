package ru.tensor.sbis.viewer.decl.viewer

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Аргументы просмотрщика изображений
 *
 * @property id             Идентификатор
 * @property imageSource    Источник изображения
 * @property zoomToBorders  Принудительно зуммировать изображения до краёв доступной области (экрана)
 *
 * @author sa.nikitin
 */
@Parcelize
open class ImageViewerArgs @JvmOverloads constructor(
    open val imageSource: ImageSource,
    override val id: String = imageSource.id,
    override var title: String? = null,
    val zoomToBorders: Boolean = false
) : ViewerArgs

/**
 * Источник изображения
 *
 * @property id Идентификатор изображения
 *
 * @author sa.nikitin
 */
sealed class ImageSource(val id: String) : Parcelable

/** @SelfDocumented */
@Parcelize
class ImageUri(val uri: String) : ImageSource(uri)

/** @SelfDocumented */
@Parcelize
class ImageBitmap(val bitmap: Bitmap) : ImageSource(bitmap.toString())
