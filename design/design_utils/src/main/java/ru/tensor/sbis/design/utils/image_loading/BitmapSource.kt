package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap

/**
 * Изображение
 *
 * @author us.bessonov
 */
sealed class BitmapSource

/**
 * Изображение, явно указываемое как [Bitmap]
 */
data class RawBitmap(val bitmap: Bitmap) : BitmapSource() {

    override fun toString() = "RawBitmap($bitmap, isRecycled = ${bitmap.isRecycled})"
}

/**
 * Изображение, загружаемое по [imageUrl]
 */
data class ImageUrl(val imageUrl: String) : BitmapSource()