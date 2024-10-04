package ru.tensor.sbis.design.profile.personcollage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.Px
import androidx.core.graphics.withClip
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.utils.image_loading.CollageBuilder
import java.lang.Integer.max

/**
 * Класс отвечающий за построение коллажа в [PersonCollageView].
 *
 * @author da.zolotarev
 */
internal class PersonCollageWithSeparatorBuilder(@Px var separatorWidth: Int = 0) : CollageBuilder() {
    private val halfSeparatorWidth
        get() = max(separatorWidth / 2, 1)

    override fun combine(b1: Bitmap, b2: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val canvas = Canvas(res).apply { drawColor(Color.WHITE) }

        var matrix = createScaleMatrix(b1.width, b1.height, halfW - halfSeparatorWidth, h)
        canvas.withClip(0, 0, halfW - halfSeparatorWidth, h) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(b2.width, b2.height, halfW + halfSeparatorWidth, h)
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW + halfSeparatorWidth, h) {
            canvas.drawBitmap(b2, matrix, null)
        }
        return res
    }

    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val halfH = h / 2
        val canvas = Canvas(res).apply { drawColor(Color.WHITE) }

        var matrix = createScaleMatrix(
            b1.width,
            b1.height,
            halfW - halfSeparatorWidth,
            h
        )
        canvas.withClip(0, 0, halfW - halfSeparatorWidth, h) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(
            b2.width,
            b2.height,
            halfW + halfSeparatorWidth,
            halfH - halfSeparatorWidth
        )
        canvas.withTranslation(halfW.toFloat(), 0F) {
            canvas.withClip(0, 0, halfW + halfSeparatorWidth, halfH - halfSeparatorWidth) {
                canvas.drawBitmap(b2, matrix, null)
            }
        }

        matrix = createScaleMatrix(
            b3.width,
            b3.height,
            halfW + halfSeparatorWidth,
            halfH + halfSeparatorWidth
        )
        canvas.translate(halfW.toFloat(), halfH.toFloat())
        canvas.withClip(0, 0, halfW + halfSeparatorWidth, halfH + halfSeparatorWidth) {
            canvas.drawBitmap(b3, matrix, null)
        }
        return res
    }

    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, b4: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val halfH = h / 2
        val canvas = Canvas(res).apply { drawColor(Color.WHITE) }

        var matrix = createScaleMatrix(
            b1.width,
            b1.height,
            halfW - halfSeparatorWidth,
            halfH - halfSeparatorWidth
        )
        canvas.withClip(0, 0, halfW - halfSeparatorWidth, halfH - halfSeparatorWidth) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(
            b2.width,
            b2.height,
            halfW + halfSeparatorWidth,
            halfH - halfSeparatorWidth
        )
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW + halfSeparatorWidth, halfH - halfSeparatorWidth) {
            canvas.drawBitmap(b2, matrix, null)
        }

        matrix = createScaleMatrix(
            b3.width,
            b3.height,
            halfW - halfSeparatorWidth,
            halfH + halfSeparatorWidth
        )
        canvas.translate(-halfW.toFloat(), halfH.toFloat())
        canvas.withClip(0, 0, halfW - halfSeparatorWidth, halfH + halfSeparatorWidth) {
            canvas.drawBitmap(b3, matrix, null)
        }

        matrix = createScaleMatrix(
            b4.width,
            b4.height,
            halfW + halfSeparatorWidth,
            halfH + halfSeparatorWidth
        )
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW + halfSeparatorWidth, halfH + halfSeparatorWidth) {
            canvas.drawBitmap(b4, matrix, null)
        }
        return res
    }
}