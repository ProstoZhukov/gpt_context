package ru.tensor.sbis.common.util

import androidx.annotation.Px

private const val CAPACITY = 100

/**
 * Ссылка на изображение, с указанием его размера.
 *
 * @author us.bessonov
 */
internal data class UrlWithSize(val url: String, @Px val size: Int)

/**
 * Кэш для хранения уже созданных url по запросу [UrlUtils.getPhotoUrlById].
 *
 * @author us.bessonov
 */
internal object PhotoUrlByIdCache {

    private val cache = LinkedHashMap<String, UrlWithSize>()

    /** @SelfDocumented */
    @JvmStatic
    fun get(photoId: String): UrlWithSize? = cache[photoId]

    /** @SelfDocumented */
    @JvmStatic
    fun put(photoId: String, url: String, @Px size: Int) = with(cache) {
        if (!containsKey(photoId) && this.size >= CAPACITY) {
            remove(keys.first())
        }
        cache[photoId] = UrlWithSize(url, size)
    }

}


