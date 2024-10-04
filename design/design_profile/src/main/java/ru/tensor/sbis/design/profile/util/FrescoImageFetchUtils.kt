/**
 * Инструменты по загрузке изображений для компонентов фото сотрудника.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.profile.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.Px
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
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.utils.checkNotNullSafe

/**
 * Результат получения [Bitmap]'a с индексом ассоциируемого элемента.
 */
internal data class BitmapResult(val bitmap: Bitmap?, val index: Int)

/**
 * Возвращает [Observable] с результатами загрузки заданных изображений.
 */
internal fun loadBitmaps(context: Context, dataList: List<PhotoData>, @Px photoSize: Int): Observable<BitmapResult> {
    return Observable.create { emitter ->
        val imageRequests = dataList.map { getImageRequest(it, photoSize) }
        val dataSources = imageRequests.mapIndexed { i, it ->
            it?.let {
                Fresco.getImagePipeline().fetchDecodedImage(it, context).apply {
                    subscribe(
                        ImageSubscriber(emitter, i),
                        Fresco.getImagePipeline().config.executorSupplier.forBackgroundTasks()
                    )
                }
            } ?: run {
                emitter.deliverResult(null, i)
                null
            }
        }
        emitter.setCancellable { dataSources.forEach { it?.close() } }
    }
}

/**
 * Возвращает `true`, если удалось установить во [view] изображение из bitmap кэша.
 */
internal fun setBitmapFromCache(data: PhotoData, @Px photoSize: Int, view: PersonImageView): Boolean {
    val imageRequest = getImageRequest(data, photoSize)
    if (!Fresco.getImagePipeline().isInBitmapMemoryCache(imageRequest)) {
        return false
    }
    view.setBitmap(getBitmapFromCache(imageRequest))
    return true
}

private fun getBitmapFromCache(imageRequest: ImageRequest?): Bitmap? = with(Fresco.getImagePipeline()) {
    val key = cacheKeyFactory.getBitmapCacheKey(imageRequest, null)
    val bitmapRef = bitmapMemoryCache.get(key)
    val bitmap = (bitmapRef?.get() as? CloseableBitmap)
        ?.underlyingBitmap
        ?.takeUnless { it.isRecycled }
    bitmapRef?.close()
    return checkNotNullSafe(bitmap) {
        "Cannot get bitmap from cache by request $imageRequest"
    }
}

private fun getImageRequest(data: PhotoData, @Px photoSize: Int): ImageRequest? {
    return data.photoUrl?.takeUnless { it.isEmpty() }
        ?.let {
            val uri = Uri.parse(getPreviewerPhotoUri(it, photoSize))
            val imageDecodeOptions = ImageDecodeOptions.newBuilder()
                .apply { decodePreviewFrame = true }
                .build()
            ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setImageDecodeOptions(imageDecodeOptions)
                .build()
        }
}

private class ImageSubscriber(private val emitter: ObservableEmitter<BitmapResult>, val index: Int) :
    BaseDataSubscriber<CloseableReference<CloseableImage>>() {

    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
        deliverEmptyResult()
    }

    override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
        if (!dataSource.isFinished) return

        val reference = dataSource.result
        val bitmap = when (val result = reference?.get()) {
            is CloseableBitmap -> result.underlyingBitmap
            is CloseableAnimatedImage -> result.imageResult?.previewBitmap?.get()
            else -> null
        }
        bitmap
            ?.takeUnless { it.isRecycled }
            ?.let { emitter.deliverResult(it, index) }
        reference?.close()
    }

    private fun deliverEmptyResult() = emitter.deliverResult(null, index)
}

private fun ObservableEmitter<BitmapResult>.deliverResult(bitmap: Bitmap?, index: Int) {
    if (!isDisposed) onNext(BitmapResult(bitmap, index))
}