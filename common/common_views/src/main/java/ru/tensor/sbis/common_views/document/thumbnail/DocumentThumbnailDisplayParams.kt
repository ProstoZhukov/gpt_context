package ru.tensor.sbis.common_views.document.thumbnail

import androidx.annotation.Px

/**
 * Параметры отображения миниатюры документа.
 *
 * @author sa.nikitin
 */
class DocumentThumbnailDisplayParams @JvmOverloads constructor(
    @Px override val totalSize: Int,
    @Px override val placeholderSize: Int = totalSize,
    @Px override val overlaySize: Int = totalSize
) : ThumbnailDisplayParams