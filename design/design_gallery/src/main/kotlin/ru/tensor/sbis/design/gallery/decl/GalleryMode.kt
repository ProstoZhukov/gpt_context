package ru.tensor.sbis.design.gallery.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Режим работы фрагмента галереи
 */
sealed interface GalleryMode : Parcelable {

    /**
     * Режим "Все медиа" - отображение всех фото/видео из галереи без возможности перемещения по альбомам
     */
    @Parcelize
    class AllMedia : GalleryMode

    /**
     * Режим "Альбомы" - отображение всех фото/видео из галереи с возможностью перемещения по альбомам
     */
    @Parcelize
    class ByAlbums : GalleryMode
}