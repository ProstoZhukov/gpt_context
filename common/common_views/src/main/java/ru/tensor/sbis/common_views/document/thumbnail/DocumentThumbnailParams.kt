package ru.tensor.sbis.common_views.document.thumbnail

import android.graphics.drawable.Drawable
import android.net.Uri

/**
 * Параметры миниатюры документа.
 *
 * @author sa.nikitin
 */
class DocumentThumbnailParams @JvmOverloads constructor(
    override val icon: Drawable,
    override val uri: Uri? = null
) : ThumbnailParams