package ru.tensor.sbis.design.profile.person

import androidx.annotation.Px

private const val CAPACITY = 100

private data class UrlWithSize(val url: String, @Px val size: Int)

/**
 * Позволяет избежать повторной загрузки изображений, которые недавно были загружены в большем размере.
 *
 * @author us.bessonov
 */
internal object RecentPhotoUrlCache {

    private val maxSizeForUrls = LinkedHashMap<String, UrlWithSize>()

    /**
     * Возвращает ссылку на изображение, подставляя в url заданный размер, либо возвращает ссылку на запрошенное ранее
     * аналогичное изображение большего размера. Если шаблон для подстановки отсутствует, возвращает оригинальный url.
     */
    fun getPhotoUrlForSize(originalUrl: String, @Px size: Int): String {
        maxSizeForUrls[originalUrl]?.let {
            if (it.size >= size) return it.url
        }

        val sizeSetResult = replacePreviewerUrlPartWithCheck(originalUrl, size, size)
        if (sizeSetResult.isSizeSet) {
            maxSizeForUrls.apply {
                if (!containsKey(originalUrl) && this.size >= CAPACITY) {
                    remove(keys.first())
                }
                put(originalUrl, UrlWithSize(sizeSetResult.url, size))
            }
        }

        return sizeSetResult.url
    }
}