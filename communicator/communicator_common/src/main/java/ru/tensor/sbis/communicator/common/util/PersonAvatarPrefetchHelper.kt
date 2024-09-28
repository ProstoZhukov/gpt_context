package ru.tensor.sbis.communicator.common.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.profiles.generated.Person
import timber.log.Timber

/**
 * Вспомогательная реализация для подготови memoryCache фрески для возможности синхронного отображения аватарок.
 *
 * @author vv.chekurda
 */
class PersonAvatarPrefetchHelper(private val appContext: Context) {

    companion object {

        /**
         * Размер аватарок в dp для реестров.
         */
        const val REGISTRY_AVATAR_SIZE_DP = 55
    }

    private val resources: Resources
        get() = appContext.resources

    private var alreadyCachedUrls = mutableSetOf<String>()

    /**
     * Подготовить битмапы для аватарок [persons] или картинки [photoUrl] с размерами в dp [photoSizeDp].
     */
    fun prefetchBitmaps(persons: List<Person>, photoUrl: String? = null, photoSizeDp: Int) {
        try {
            val uris = getUris(persons, photoUrl, photoSizeDp)
            uris.forEach { uri ->
                performPrefetch(createImageRequest(Uri.parse(uri)))
            }
        } catch (ex: Exception) {
            Timber.e(ex, "PersonAvatarPrefetchHelper.prefetchBitmaps")
        }
    }

    private fun getUris(persons: List<Person>, photoUrl: String? = null, photoSizeDp: Int): Set<String> =
        getAvatarSet(photoUrl, photoSizeDp) ?: getCollageSet(persons, photoSizeDp)

    private fun getAvatarSet(photoUrl: String? = null, photoSizeDp: Int): Set<String>? =
        photoUrl?.takeIf {
            !alreadyCachedUrls.contains(it) && it.isNotBlank()
        }?.let {
            alreadyCachedUrls.add(it)
            setOf(prepareUri(it, photoSizeDp, PreviewerUrlUtil.ScaleMode.CROP))
        }

    private fun getCollageSet(persons: List<Person>, photoSizeDp: Int): Set<String> =
        persons.mapNotNull {
            it.photoUrl?.takeIf { url -> url.isNotEmpty() }
        }.map { prepareUri(it, photoSizeDp, PreviewerUrlUtil.ScaleMode.RESIZE) }
            .filter { !alreadyCachedUrls.contains(it) }
            .also { alreadyCachedUrls.addAll(it) }
            .toSet()

    fun prepareUri(
        originUrl: String,
        photoSizeDp: Int,
        mode: PreviewerUrlUtil.ScaleMode,
    ): String {
        val sizePx = resources.dp(photoSizeDp)
        return PreviewerUrlUtil.formatImageUrl(
            originUrl,
            sizePx,
            sizePx,
            mode
        )!!
    }

    private fun createImageRequest(uri: Uri): ImageRequest {
        val imageDecodeOptions = ImageDecodeOptions.newBuilder()
            .apply { decodePreviewFrame = true }
            .build()
        return ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setImageDecodeOptions(imageDecodeOptions)
            .build()
    }

    private fun performPrefetch(request: ImageRequest) {
        Timber.d("PersonAvatarPrefetchHelper.prefetch ${request.sourceUri}")
        Fresco.getImagePipeline()
            .fetchDecodedImage(request, null)
            .subscribe(
                object : BaseBitmapDataSubscriber() {
                    override fun onNewResultImpl(bitmap: Bitmap?) {
                        Timber.d("PersonAvatarPrefetchHelper.prefetch success")
                    }

                    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                        Timber.d("PersonAvatarPrefetchHelper.prefetch fail")
                    }
                },
                Fresco.getImagePipeline().config.executorSupplier.forBackgroundTasks()
            )
    }
}