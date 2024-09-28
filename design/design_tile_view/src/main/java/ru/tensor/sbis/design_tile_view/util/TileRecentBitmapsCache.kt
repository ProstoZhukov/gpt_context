package ru.tensor.sbis.design_tile_view.util

import android.graphics.Bitmap
import androidx.annotation.Px

private const val CAPACITY = 64

/**
 * Ключ кэша изображений плитки
 *
 * @author us.bessonov
 */
internal data class RecentBitmapsCacheKey(
    val urls: List<String>,
    val tintMode: TintMode
)

/**
 * Кэш недавних изображений, отображаемых в компоненте Плитка
 *
 * @author us.bessonov
 */
internal object TileRecentBitmapsCache {

    private val bitmaps = LinkedHashMap<RecentBitmapsCacheKey, Bitmap>()

    /** @SelfDocumented */
    fun get(key: RecentBitmapsCacheKey, @Px width: Int, @Px height: Int): Bitmap? =
        bitmaps[key]?.takeIf {
            it.width >= width || height == 0 || it.width / it.height.toFloat() != width / height.toFloat()
        }

    /** @SelfDocumented */
    fun put(bitmap: Bitmap, key: RecentBitmapsCacheKey) = with(bitmaps) {
        bitmaps[key]?.let { current ->
            if (bitmap.width > current.width) {
                put(key, bitmap)
            }
            return@with
        }
        if (size >= CAPACITY) remove(keys.first())
        put(key, bitmap)
    }

    /** @SelfDocumented */
    fun createKey(urls: List<String>?, tintMode: TintMode) = urls?.let { RecentBitmapsCacheKey(urls, tintMode) }
}