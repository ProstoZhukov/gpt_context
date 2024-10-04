/**
 * Утилиты для работы с [Bitmap].
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.utils

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import androidx.annotation.Px

/**
 * Создаёт модифицируемую копию, если оригинал не соответствует размерам [targetWidth] и [targetHeight] или не
 * поддерживает модификации.
 *
 * @param targetWidth целевая ширина изображения
 * @param targetHeight целевая высота изображения
 * @param recycleInput отметка о необходимости удалить оригинальное изображение. Не применимо, если оригинальное
 * изображение соответствует по размерам и пригодно к модификации
 */
fun Bitmap.asMutableBitmap(@Px targetWidth: Int, @Px targetHeight: Int, recycleInput: Boolean): Bitmap =
    if (targetWidth > 0 && targetHeight > 0 && (width != targetWidth || height != targetHeight)) {
        val thumbnail = ThumbnailUtils.extractThumbnail(
            this,
            targetWidth,
            targetHeight,
            if (recycleInput) ThumbnailUtils.OPTIONS_RECYCLE_INPUT else 0
        )
        if (thumbnail.isMutable) {
            thumbnail
        } else {
            val mutableCopy = thumbnail.copy(config, true)
            thumbnail.recycle()
            mutableCopy
        }
    } else if (!isMutable) {
        val mutableCopy = copy(config, true)
        if (recycleInput) {
            recycle()
        }
        mutableCopy
    } else {
        this
    }