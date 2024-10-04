package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.os.Parcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * "Элемент" бизнес-логики галереи (фото или видео)
 *
 * @property id         Уникальный идентификатор, присваиваемый системой
 * @property bucketId   Уникальный идентификатор альбома, содержащего данный элемент
 * @property dateTaken  Дата создания фото/видео
 * @property uri        URI в виде строки, ссылающийся на файл или фото из буфера обмена со схемой content.
 * @property isVideo    Видео ли
 * @property duration   Если видео, длительность (при фото null)
 * @property width      Ширина фото
 * @property height     Длина фото
 * @property size       Размер файла в байтах
 */
@Parcelize
data class GalleryItem(
    val id: Int?,
    val bucketId: Int,
    val dateTaken: Long?,
    val uri: String,
    val isVideo: Boolean = false,
    val duration: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val size: Long? = null,
) : Parcelable {

    /** Порядковый номер выбора */
    @IgnoredOnParcel
    val selectionNumber = MutableStateFlow<Int?>(null)
}