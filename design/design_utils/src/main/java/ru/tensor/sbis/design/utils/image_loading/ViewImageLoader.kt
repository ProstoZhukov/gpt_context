package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap
import android.view.View
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.tensor.sbis.design.utils.image_loading.reloadmanager.ImageLoadingView
import ru.tensor.sbis.design.utils.image_loading.reloadmanager.ImageReloadManager

/**
 * Реализует механику загрузки и отображения изображений.
 *
 * При использовании, во [View] нужно переопределить вызовы [View.onMeasure] и [View.onVisibilityAggregated], добавив
 * вызовы [onViewMeasured] и [onVisibilityAggregated] соответственно.
 *
 * [collageBuilder] - Описывает способ построения коллажа
 *
 * @author us.bessonov
 */
class ViewImageLoader(
    private val collageBuilder: CollageBuilder = StubCollageBuilder(),
    private val diagnosticsId: Int? = null
) : ImageLoadingView {

    private lateinit var view: View
    private lateinit var drawableImageView: DrawableImageView
    private lateinit var putPreparedBitmapToCache: () -> Unit
    private lateinit var getPreparedBitmapFromCache: () -> Bitmap?
    private lateinit var getImageWidthAndHeight: () -> Pair<Int, Int>

    private var images: List<BitmapSource> = emptyList()

    private val imageLoadingResult = mutableListOf<ImageLoadingResult?>()

    private var imageLoadingJob: Job? = null

    private var isHidden = false

    private var isResultDeferred = false

    private val coroutineScope = MainScope() + CoroutineName("Tile view scope")

    override val isLoading: Boolean
        get() = hasImage() && imageLoadingResult.any { it == null }

    override val isLoadingFailedBecauseIoException: Boolean
        get() = imageLoadingResult.any { (it as? Failure)?.isCausedByIoException == true }

    /**
     * @param putPreparedBitmapToCache выполняет кэширование готового к отображению сложного изображения.
     * @param getPreparedBitmapFromCache возвращает готовое к отображению изображение из вспомогательного кэша.
     */
    fun init(
        view: View,
        drawableImageView: DrawableImageView,
        putPreparedBitmapToCache: () -> Unit,
        getPreparedBitmapFromCache: () -> Bitmap?,
        getImageWidthAndHeight: () -> Pair<Int, Int>
    ) {
        this.view = view
        this.drawableImageView = drawableImageView
        this.putPreparedBitmapToCache = putPreparedBitmapToCache
        this.getPreparedBitmapFromCache = getPreparedBitmapFromCache
        this.getImageWidthAndHeight = getImageWidthAndHeight
        ImageReloadManager.attach(this)
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = onAttachedToWindow()

            override fun onViewDetachedFromWindow(v: View) = onDetachedFromWindow()
        })
    }

    /** @SelfDocumented */
    fun setImages(images: List<BitmapSource>, skipCache: Boolean = false) {
        drawableImageView.setBitmap(null)
        this.images = images
        val usedImages = getDisplayedImages(images)
        if (usedImages.all { it is ImageUrl }) {
            getPreparedBitmapFromCache()?.let {
                updateImageLoadingResult(listOf(BitmapResult(BitmapReference(it))))
                drawableImageView.setPreparedBitmap(it)
                return
            }
        }

        val results: List<ImageLoadingResult?> = getBitmapsFromCache(usedImages)
            .also { updateImageLoadingResult(it) }
            .toMutableList()
        val singleResult = results.singleOrNull()
        if (singleResult is BitmapResult) {
            log("cached single result")
            imageLoadingJob?.cancel("Image is set from cache")
            displayBitmapIfPossible(singleResult.bitmap)
            return
        }

        launchImageLoading {
            if (usedImages.size > 1) buildAndDisplayCollageIfPossible()
            if (results.all { it is BitmapResult }) return@launchImageLoading
            getBitmaps(usedImages, skipCache = skipCache)
                .collect { result ->
                    updateImageLoadingResult(result)
                    log("received result at ${result.index}: $result")
                    buildAndDisplayCollageIfPossible()
                    if (results.all { it is BitmapResult || it is NotRequested }) {
                        putPreparedBitmapToCache()
                    }
                }
        }
    }

    /**
     * Вызывается из [View.onVisibilityAggregated]
     */
    fun onVisibilityAggregated(isVisible: Boolean) {
        if (isHidden && isVisible && drawableImageView.isBitmapRecycled()) {
            setImages(images)
        }
        isHidden = !isVisible
    }

    /**
     * Вызывается из [View.onMeasure]
     */
    fun onViewMeasured() {
        if (!isResultDeferred) return
        isResultDeferred = false
        log("process result after measured")
        launchImageLoading { buildAndDisplayCollageIfPossible() }
    }

    /**
     * @SelfDocumented
     */
    fun clearImages(){
        drawableImageView.setBitmap(null)
        this.images = emptyList()
    }

    /** @SelfDocumented */
    fun hasResult() = images.isEmpty() || imageLoadingResult.any { it is BitmapResult }

    override fun addOnAttachStateChangeListener(listener: View.OnAttachStateChangeListener) {
        view.addOnAttachStateChangeListener(listener)
    }

    override fun reloadImage() {
        if (images.isNotEmpty()) setImages(images, skipCache = drawableImageView.isBitmapRecycled())
    }

    override fun toString() = "ViewImageLoader#$diagnosticsId(data: ${images}, result: ${imageLoadingResult})"

    private fun onAttachedToWindow() {
        log("attached to window")
        if (shouldReloadImage()) reloadImage()
    }

    private fun onDetachedFromWindow() {
        log("detached from window")
        imageLoadingJob?.cancel("View detached from window")
        imageLoadingJob = null
        closeBitmapReferences()
    }


    private fun hasImage() = images.isNotEmpty()

    private fun shouldReloadImage() = isLoading || hasImage() && !drawableImageView.hasValidBitmap()

    private fun getDisplayedImages(images: List<BitmapSource>): List<BitmapSource> {
        val (validImages, emptyImages) = images.partition { it !is ImageUrl || it.imageUrl.isNotBlank() }
        return validImages.plus(emptyImages.take(1)).take(4)
    }

    private fun launchImageLoading(getBitmaps: suspend CoroutineScope.() -> Unit) {
        imageLoadingJob?.cancel("New image requested")
        imageLoadingJob = coroutineScope.launch { getBitmaps() }
    }

    private fun displayBitmapIfPossible(bitmap: Bitmap) {
        val (width, height) = getImageWidthAndHeight()
        if (width > 0 && height > 0) {
            drawableImageView.setBitmap(bitmap)
            view.invalidate()
        } else displayResultAfterLayout()
    }

    private fun displayResultAfterLayout() {
        if (hasImage()) {
            isResultDeferred = true
            if (!view.isLayoutRequested) {
                view.invalidate()
                view.requestLayout()
            }
        }
    }

    private suspend fun buildAndDisplayCollageIfPossible() {
        val (width, height) = getImageWidthAndHeight()
        log("build and display collage. results $imageLoadingResult")
        if (width > 0 && height > 0) {
            drawableImageView.setBitmap(buildCollage(imageLoadingResult, width, height))
            log("result shown immediately")
            view.invalidate()
        } else {
            log("result is waiting for view to be measured")
            displayResultAfterLayout()
        }
    }

    private suspend fun buildCollage(bitmapResults: List<ImageLoadingResult?>, width: Int, height: Int): Bitmap? {
        if (bitmapResults.size == 1 && bitmapResults.single() !is BitmapResult) return null
        val bitmaps = bitmapResults.mapNotNull {
            when (it) {
                is BitmapResult -> it.bitmap
                else -> drawableImageView.getPlaceholderBitmap(width, height, false)
            }
        }
        return collageBuilder.buildCollage(bitmaps, width, height)
    }

    private fun log(msg: String) = diagnosticsId?.let { ImageLoaderDiagnostics.log(it, msg) }

    private fun updateImageLoadingResult(result: ImageLoadingResult) {
        try {
            (imageLoadingResult.getOrNull(result.index) as? BitmapResult?)?.closeReference()
            imageLoadingResult[result.index] = result
        } catch (e: Exception) {
            ImageLoaderDiagnostics.error(diagnosticsId, "Cannot update result at ${result.index}")
        }
    }

    private fun updateImageLoadingResult(results: List<ImageLoadingResult?>) {
        imageLoadingResult.clear()
        closeBitmapReferences()
        imageLoadingResult.addAll(results)
    }

    private fun closeBitmapReferences() {
        imageLoadingResult.filterIsInstance<BitmapResult>().forEach { it.closeReference() }
    }
}