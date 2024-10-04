package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.annotation.Px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Класс отвечающий за способ постройки коллажа в [ViewImageLoader] до 4 изображений
 *
 * Чтобы реализовать свой необходимо определить правила объединения изображений от 2 до 4
 *
 * @author da.zolotarev
 */
abstract class CollageBuilder {

    /**
     * Постройка коллажа из переданных [bitmaps] (не более 4)
     */
    suspend fun buildCollage(
        bitmaps: List<Bitmap>,
        @Px width: Int,
        @Px height: Int,
        dispatcher: CoroutineContext = Dispatchers.Default
    ) = withContext(dispatcher) {
        when (bitmaps.size) {
            0 -> null
            1 -> bitmaps.single()
            2 -> combine(bitmaps.first(), bitmaps.last(), width, height)
            3 -> combine(bitmaps[0], bitmaps[1], bitmaps[2], width, height)
            4 -> combine(bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], width, height)
            else -> error("Unexpected images count ${bitmaps.size}")
        }
    }

    protected abstract fun combine(b1: Bitmap, b2: Bitmap, @Px w: Int, @Px h: Int): Bitmap
    protected abstract fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, @Px w: Int, @Px h: Int): Bitmap
    protected abstract fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, b4: Bitmap, @Px w: Int, @Px h: Int): Bitmap

    /**
     * Утилитная функция, позволяющая создать матрицу масштабирования изображения под измененный размер
     */
    protected fun createScaleMatrix(srcW: Int, srcH: Int, destW: Int, destH: Int) = Matrix().apply {
        val scaleX = destW.toFloat() / srcW
        val scaleY = destH.toFloat() / srcH
        val scale = maxOf(scaleX, scaleY)

        val scaledW = srcW * scale
        val scaledH = srcH * scale

        postScale(scale, scale)
        postTranslate(-(scaledW - destW) / 2, -(scaledH - destH) / 2)
    }
}