/**
 * Инструменты по загрузке изображений для компонента Плитка
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap
import android.net.Uri
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.image.CloseableAnimatedImage
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Возвращает [Flow], в котором публикуются результаты загрузки изображений
 */
internal fun getBitmaps(images: List<BitmapSource>, skipCache: Boolean): Flow<ImageLoadingResult> {
    return flow {
        images.forEachIndexed { i, img ->
            emit(
                when (img) {
                    is RawBitmap -> BitmapResult(BitmapReference(img.bitmap), i)
                    is ImageUrl -> loadBitmap(img.imageUrl, i, skipCache)
                }
            )
        }
    }
}

/**
 * Возвращает [Bitmap] - загрузки заданного изображения
 */
internal suspend fun loadBitmap(
    url: String,
    index: Int,
    skipCache: Boolean,
    dispatcher: CoroutineContext = Dispatchers.IO
): ImageLoadingResult = withContext(dispatcher) {
    try {
        if (url.isEmpty()) NotRequested(index) else BitmapResult(launchBitmapLoading(url, skipCache), index)
    } catch (ex: Exception) {
        if (ex is CancellationException) {
            throw ex
        } else {
            Timber.d(ex, "loadBitmap failed url - $url")
            Failure(isCausedByIoException = ex.cause is IOException, index, ex)
        }
    }
}

/**
 * Возвращает список из [Bitmap] для заданных url, если все они были обнаружены в кэше.
 * [RawBitmap] в списке будет соответствовать исходное изображение
 */
internal fun getBitmapsFromCache(images: List<BitmapSource>): List<BitmapResult?> {
    return images.mapIndexed { i, it ->
        when (it) {
            is RawBitmap -> BitmapResult(BitmapReference(it.bitmap), i)
            is ImageUrl -> getBitmapFromCache(it.imageUrl)?.let { BitmapResult(it, i) }
        }
    }
}

private fun getBitmapFromCache(url: String): BitmapReference? {
    val imageRequest = getImageRequest(url, false)
    if (!Fresco.getImagePipeline().isInBitmapMemoryCache(imageRequest)) {
        return null
    }
    return getBitmapFromCache(imageRequest)
}

private suspend fun launchBitmapLoading(
    url: String,
    skipCache: Boolean
) = suspendCancellableCoroutine<BitmapReference> { continuation ->
    val dataSource = Fresco.getImagePipeline()
        .fetchDecodedImage(getImageRequest(url, skipCache), null)
        .apply {
            subscribe(
                ImageSubscriber(continuation),
                Fresco.getImagePipeline().config.executorSupplier.forBackgroundTasks()
            )
        }
    continuation.invokeOnCancellation { dataSource.close() }
}

private fun getBitmapFromCache(imageRequest: ImageRequest?): BitmapReference? =
    with(Fresco.getImagePipeline()) {
        val key = cacheKeyFactory.getBitmapCacheKey(imageRequest, null)
        val bitmapRef = bitmapMemoryCache.get(key)
        val bitmap = (bitmapRef?.get() as? CloseableBitmap)
            ?.underlyingBitmap
            ?.takeUnless { it.isRecycled }
        return bitmap?.let { BitmapReference(it, bitmapRef) }
    }

private fun getImageRequest(url: String, skipCache: Boolean): ImageRequest {
    val uri = Uri.parse(url)
    val imageDecodeOptions = ImageDecodeOptions.newBuilder()
        .apply { decodePreviewFrame = true }
        .build()
    return ImageRequestBuilder
        .newBuilderWithSource(uri)
        .apply {
            if (skipCache) disableMemoryCache()
        }
        .setImageDecodeOptions(imageDecodeOptions)
        .build()
}

private class ImageSubscriber(
    private val continuation: Continuation<BitmapReference>
) : BaseDataSubscriber<CloseableReference<CloseableImage>>() {

    override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
        val result = dataSource.result
        when (val reference = result?.get()) {
            is CloseableBitmap -> processBitmapResult(reference.underlyingBitmap, result)
            is CloseableAnimatedImage -> processBitmapResult(reference.getPreviewBitmapOrNull(), result)
            null -> resumeWithLoadingError()
        }
    }

    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) =
        continuation.resumeWithException(
            IllegalStateException("Cannot load image bitmap", dataSource.failureCause)
        )

    private fun processBitmapResult(bitmap: Bitmap?, reference: CloseableReference<CloseableImage>) = when {
        bitmap == null -> resumeWithLoadingError()
        bitmap.isRecycled -> continuation.resumeWithException(IllegalStateException("Bitmap is recycled"))
        else -> continuation.resume(BitmapReference(bitmap, reference))
    }

    private fun resumeWithLoadingError() =
        continuation.resumeWithException(IllegalStateException("Image is not loaded"))

    private fun CloseableAnimatedImage.getPreviewBitmapOrNull(): Bitmap? = imageResult?.run {
        previewBitmap?.get()
            ?: Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888).apply {
                image.getFrame(frameForPreview).renderFrame(width, height, this)
            }
    }
}
