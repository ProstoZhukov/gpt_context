package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap

/**
 * Заглушка билдера коллажей
 *
 * @author da.zolotarev
 */
internal class StubCollageBuilder : CollageBuilder() {
    override fun combine(b1: Bitmap, b2: Bitmap, w: Int, h: Int) = b1
    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, w: Int, h: Int) = b1
    override fun combine(b1: Bitmap, b2: Bitmap, b3: Bitmap, b4: Bitmap, w: Int, h: Int) = b1
}