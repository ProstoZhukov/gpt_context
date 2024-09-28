package ru.tensor.sbis.design.profile.util

import android.net.Uri
import androidx.annotation.Px
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.DraweeView
import com.facebook.imagepipeline.listener.BaseRequestListener
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.design.profile.person.RecentPhotoUrlCache
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import kotlin.collections.set

/**
 * Инструмент для упрощения предварительной загрузки изображений из кэша постоянной памяти в bitmap-кэш для возможности
 * их немедленного отображения в [DraweeView], минуя заглушки и fade-in.
 *
 * @author us.bessonov
 */
object ImageBitmapPreFetcher {

    private val preFetchSources = mutableMapOf<Uri, DataSource<Void>>()
    private val requestListener = object : BaseRequestListener() {
        override fun onRequestSuccess(request: ImageRequest, requestId: String?, isPrefetch: Boolean) {
            preFetchSources.remove(request.sourceUri)
        }

        override fun onRequestFailure(
            request: ImageRequest,
            requestId: String?,
            throwable: Throwable?,
            isPrefetch: Boolean
        ) {
            preFetchSources.remove(request.sourceUri)
        }
    }

    /**
     * Обеспечивает наличие заданных изображений в кэше.
     * Достаточно обеспечить подготовку первых нескольких элементов, отображение которых ожидается на экране.
     *
     * @param photoSize опциональный размер фото для подстановки в url (одно из значений [PhotoSize.photoSize])
     */
    fun prefetchPhotos(items: List<PhotoData>, @Px photoSize: Int? = null) {
        val urls = mutableListOf<String?>().apply {
            items.forEach {
                when (it) {
                    is DepartmentData -> addAll(it.persons.map(PersonData::photoUrl))
                    else -> add(it.photoUrl)
                }
            }
        }
        prefetch(urls, photoSize)
    }

    /**
     * Обеспечивает наличие заданных изображений в кэше.
     * Достаточно обеспечить подготовку первых нескольких элементов, отображение которых ожидается на экране.
     *
     * @param urls список подготавливаемых url (допускаются повторяющиеся и `null`)
     * @param photoSize опциональный размер фото для подстановки в url (одно из значений [PhotoSize.photoSize])
     */
    fun prefetch(urls: List<String?>, @Px photoSize: Int? = null) {
        urls.filterNotNull().toSet()
            .forEach { prefetch(it, photoSize) }
    }

    /**
     * Подгружает заданное изображение в кэш, если оно отсутствует в нём.
     *
     * @param photoSize опциональный размер фото для подстановки в url (одно из значений [PhotoSize.photoSize])
     */
    fun prefetch(url: String, @Px photoSize: Int? = null) {
        val uri = photoSize
            ?.let { Uri.parse(RecentPhotoUrlCache.getPhotoUrlForSize(url, it)) }
            ?: Uri.parse(url)
        val pipeline = Fresco.getImagePipeline()
        if (preFetchSources.containsKey(uri)) return
        val request = ImageRequestBuilder.newBuilderWithSource(uri).build()
        if (pipeline.isInBitmapMemoryCache(request)) return
        preFetchSources[uri] = pipeline.prefetchToBitmapCache(request, null, requestListener)
    }
}
