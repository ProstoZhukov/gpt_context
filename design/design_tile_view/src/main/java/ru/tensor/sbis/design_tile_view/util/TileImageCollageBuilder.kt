package ru.tensor.sbis.design_tile_view.util

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.withClip
import ru.tensor.sbis.design.utils.image_loading.CollageBuilder
import ru.tensor.sbis.design_tile_view.view.TileImageView

/**
 * Класс отвечающий за построение коллажа в [TileImageView]
 *
 * @author da.zolotarev
 */
internal class TileImageCollageBuilder : CollageBuilder() {
    override fun combine(b1: Bitmap, b2: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val canvas = Canvas(res)

        var matrix = createScaleMatrix(b1.width, b1.height, halfW, h)
        canvas.withClip(0, 0, halfW, h) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(b2.width, b2.height, halfW, h)
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW, h) {
            canvas.drawBitmap(b2, matrix, null)
        }
        return res
    }

    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val halfH = h / 2
        val canvas = Canvas(res)

        var matrix = createScaleMatrix(b1.width, b1.height, halfW, halfH)
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(b2.width, b2.height, halfW, halfH)
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b2, matrix, null)
        }

        matrix = createScaleMatrix(b3.width, b3.height, w, halfH)
        canvas.translate(-halfW.toFloat(), halfH.toFloat())
        canvas.withClip(0, 0, w, halfH) {
            canvas.drawBitmap(b3, matrix, null)
        }
        return res
    }

    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, b4: Bitmap, w: Int, h: Int): Bitmap {
        val res = Bitmap.createBitmap(w, h, b1.config)
        val halfW = w / 2
        val halfH = h / 2
        val canvas = Canvas(res)

        var matrix = createScaleMatrix(b1.width, b1.height, halfW, halfH)
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b1, matrix, null)
        }

        matrix = createScaleMatrix(b2.width, b2.height, halfW, halfH)
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b2, matrix, null)
        }

        matrix = createScaleMatrix(b3.width, b3.height, halfW, halfH)
        canvas.translate(-halfW.toFloat(), halfH.toFloat())
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b3, matrix, null)
        }

        matrix = createScaleMatrix(b4.width, b4.height, halfW, halfH)
        canvas.translate(halfW.toFloat(), 0F)
        canvas.withClip(0, 0, halfW, halfH) {
            canvas.drawBitmap(b4, matrix, null)
        }
        return res
    }
}