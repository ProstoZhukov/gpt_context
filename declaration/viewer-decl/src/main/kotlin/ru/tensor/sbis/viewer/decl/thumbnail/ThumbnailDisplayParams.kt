package ru.tensor.sbis.viewer.decl.thumbnail

import androidx.annotation.Px

/**
 * Параметры отображения миниатюры.
 *
 * @property totalSize          Размер миниатюры в пикселях.
 * @property placeholderSize    Размер иконки, замещающей миниатюру во время её загрузки.
 * @property overlaySize        Размер иконки, накладываемой поверх миниатюры.
 *
 * @author sa.nikitin
 */
interface ThumbnailDisplayParams {
    @get:Px val totalSize: Int
    @get:Px val placeholderSize: Int
    @get:Px val overlaySize: Int
}