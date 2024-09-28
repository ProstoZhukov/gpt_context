package ru.tensor.sbis.design_tile_view

import android.graphics.Bitmap

/**
 * Изображение у плитки
 *
 * @author us.bessonov
 */
sealed class TileViewImage

/**
 * Изображение, явно указываемое как [Bitmap]
 */
class TileViewBitmap(val bitmap: Bitmap) : TileViewImage() {

    override fun toString() = "TileViewBitmap($bitmap, isRecycled = ${bitmap.isRecycled})"
}

/**
 * Изображение, загружаемое по [imageUrl]
 */
data class TileViewImageUrl(val imageUrl: String) : TileViewImage()