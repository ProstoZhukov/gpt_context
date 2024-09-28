package ru.tensor.sbis.fresco_view.util.superellipse


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.facebook.cache.common.CacheKey
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.common.references.CloseableReference
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory
import com.facebook.imagepipeline.request.BasePostprocessor
import timber.log.Timber

// see: http://frescolib.org/docs/modifying-image.html usage manual
// see: http://frescolib.org/docs/resizing.html to improve performance
// see: https://github.com/facebook/fresco/tree/master/samples/showcase/src/main/java/com/facebook/fresco/samples/showcase
/**
 * Класс для выполнения постобработки растрового изображения в SuperEllipse.
 * Необходим [context] для получения ресурса маски [maskResId]. Опциональна передача цвета фона [backgroundColor]
 *
 * Created by aa.mironychev on 22.11.17.
 */
class SuperEllipsePostprocessor @JvmOverloads constructor(
    private val context: Context,
    @DrawableRes private val maskResId: Int,
    @ColorInt private val backgroundColor: Int = ContextCompat.getColor(context, android.R.color.white)
) : BasePostprocessor() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    override fun process(sourceBitmap: Bitmap, bitmapFactory: PlatformBitmapFactory): CloseableReference<Bitmap> {
        val width = sourceBitmap.width
        val height = sourceBitmap.height
        if (width == height) {
            return super.process(sourceBitmap, bitmapFactory)
        }
        // Размер bitmap нужно изменить
        val isWidthBiggerHeight = width > height
        val minLength = if (isWidthBiggerHeight) height else width
        val cropW = if (isWidthBiggerHeight) (width - height) / 2 else 0
        val cropH = if (isWidthBiggerHeight) 0 else (height - width) / 2
        val destBitmap = Bitmap.createBitmap(sourceBitmap, cropW, cropH, minLength, minLength)
        return super.process(destBitmap, bitmapFactory)
    }

    override fun process(destBitmap: Bitmap, sourceBitmap: Bitmap) {
        synchronized(this) {
            try {
                ellipsizeBitmap(sourceBitmap, destBitmap)
            } catch (outOfMemoryError: OutOfMemoryError) {
                Timber.w(outOfMemoryError, "Out of memory in postprocessor. Try to recover.")
                System.gc()
                try {
                    ellipsizeBitmap(sourceBitmap, destBitmap)
                } catch (error: OutOfMemoryError) {
                    Timber.w(error, "OutOfMemory occured twice in postprocessor. Recovery failed.")
                    if (context is Activity) {
                        context.finish()
                    }
                }
            }
        }
    }

    private fun ellipsizeBitmap(sourceBitmap: Bitmap, destBitmap: Bitmap) {
        val maskBitmap = BitmapFactory.decodeResource(context.resources, maskResId)
        val immutableBitmap = Bitmap.createScaledBitmap(
            maskBitmap,
            sourceBitmap.width,
            sourceBitmap.height,
            false
        )
        val mask = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tempCanvas = Canvas(destBitmap)
        tempCanvas.drawColor(backgroundColor)
        tempCanvas.drawBitmap(sourceBitmap, 0f, 0f, null)
        tempCanvas.drawBitmap(mask, 0f, 0f, mPaint)
        immutableBitmap.recycle()
        mask.recycle()
    }

    // Override to enable caching processed images
    override fun getPostprocessorCacheKey(): CacheKey {
        return SimpleCacheKey("Crop:SuperEllipse widh maskRes $maskResId")
    }
}