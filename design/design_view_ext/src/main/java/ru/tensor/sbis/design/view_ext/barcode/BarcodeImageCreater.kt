package ru.tensor.sbis.design.view_ext.barcode

import android.graphics.Bitmap
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import timber.log.Timber
import kotlin.math.max

/**
 * Набор расширений для создания баркодов
 */

/**@SelfDocumented*/
fun AppCompatImageView.setBarcodeImage(
    number: String,
    barcodeFormat: BarcodeFormat,
    width: Int = 0,
    height: Int = 0,
    qrColor: Long = 0xFF000000,
    backColor: Int = 0x00000000
) {
    if (number.isEmpty()) return
    val multiFormatWriter = MultiFormatWriter()
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            try {
                val qrWidth = if (width != 0) width else measuredWidth
                val qrHeight = if (height != 0) height else measuredHeight
                val bitMatrix = multiFormatWriter.encode(
                    number,
                    barcodeFormat,
                    max(qrWidth - paddingStart - paddingEnd, 0),
                    max(qrHeight - paddingTop - paddingBottom, 0),
                    mapOf(EncodeHintType.MARGIN to "0")
                )
                setImageBitmap(createBitmap(bitMatrix, qrColor.toInt(), backColor))
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            } catch (e: Exception) {
                Timber.e(e, "Can't encode barcode")
            }
        }
    })
}

private fun createBitmap(matrix: BitMatrix, qrColor: Int, backColor: Int): Bitmap {
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (matrix[x, y]) qrColor else backColor
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}