package ru.tensor.sbis.viewer.decl.thumbnail

import android.graphics.drawable.Drawable
import android.net.Uri

/**
 * Параметры миниатюры.
 *
 * @property icon   Иконка - drawable, замещающий миниатюру во время её загрузки.
 * @property uri    URI в виде строки, ссылающийся на миниатюру.
 *
 * @author sa.nikitin
 */
interface ThumbnailParams {
    val icon: Drawable
    val uri: Uri?
}