package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Элемент бизнес-логики "Альбом" (с фото и видео)
 *
 * @property id     Уникальный идентификатор, присваиваемый системой, за исключением альбомов "Все медиа", "Все фото" и "Все видео"
 * @property name   Название альбома
 */
@Parcelize
internal class GalleryAlbumItem(
    val id: Int,
    val name: String
) : Parcelable {

    @IgnoredOnParcel
    private val _items: MutableList<GalleryItem> = mutableListOf()

    /** Содержание в виде фото и видео */
    @IgnoredOnParcel
    val items: List<GalleryItem> by ::_items

    /** Превью-фото альбома */
    val coverPhoto: GalleryItem? get() = items.firstOrNull()

    fun addItem(item: GalleryItem) {
        _items.add(item)
    }

    fun sort() {
        _items.sortByDescending { it.dateTaken }
    }
}