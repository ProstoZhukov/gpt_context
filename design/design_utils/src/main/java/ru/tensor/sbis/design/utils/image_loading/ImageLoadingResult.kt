package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap
import com.facebook.common.references.CloseableReference
import com.facebook.imagepipeline.image.CloseableImage
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Результат загрузки изображения
 *
 * @author us.bessonov
 */
internal sealed class ImageLoadingResult(val index: Int)

/**
 * Bitmap для дальнейшей обработки и отображения в компоненте
 */
internal class BitmapResult(
    private val bitmapReference: BitmapReference,
    index: Int = -1
) : ImageLoadingResult(index) {

    /** @SelfDocumented */
    val bitmap: Bitmap
        get() = bitmapReference.bitmap

    /**
     * Закрыть ссылку на изображение. После этого [Bitmap] может быть утилизирован.
     */
    fun closeReference() = bitmapReference.closeableReference?.close()

    override fun toString() = "BitmapResult(index=$index, bitmap=${bitmap}, isRecycled=${bitmap.isRecycled})"
}

/**
 * Не было ссылки на изображение - загрузка не осуществлялась
 */
internal class NotRequested(index: Int) : ImageLoadingResult(index)

/**
 * При загрузке произошла ошибка.
 *
 * @property isCausedByIoException `true` если в ходе загрузки встретилось [IOException]
 */
internal class Failure(
    val isCausedByIoException: Boolean = false,
     index: Int,
    val exception: Throwable
 ) : ImageLoadingResult(index) {

    private fun getStackTraceString(ex: Throwable?): String {
        ex ?: return ""
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        ex.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    override fun toString() = "Failure(isCausedByIoException=$isCausedByIoException, index=$index)\n" +
            getStackTraceString(exception.cause)
}

/**
 * [Bitmap] и опциональная исходная ссылка, которую требуется закрыть после скрытия изображения с экрана.
 *
 * @author us.bessonov
 */
internal class BitmapReference(val bitmap: Bitmap, val closeableReference: CloseableReference<CloseableImage>? = null)