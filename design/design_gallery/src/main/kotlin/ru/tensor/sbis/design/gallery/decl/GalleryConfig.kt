package ru.tensor.sbis.design.gallery.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.files_picker.decl.GalleryCameraType
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode

/**
 * Конфиг для создания фрагмента галереи
 *
 * @property mode                   Режим работы фрагмента
 * @property selectionMode          Режим выбора: одиночный/множественный
 * @property sizeInMBytesLimit      Максимально возможный размер файла в мегабайтах
 * @property needOnlyImages         Требуются ли только фото.
 * @property cameraType             Тип камеры.
 * @property isNeedBottomPadding    Нужен ли отступ снизу от контента.
 */
@Parcelize
class GalleryConfig(
    val mode: GalleryMode,
    val selectionMode: GallerySelectionMode,
    val cameraType: GalleryCameraType = GalleryCameraType.Default(),
    val sizeInMBytesLimit: Int? = null,
    val needOnlyImages: Boolean,
    val isNeedBottomPadding: Boolean = false
) : Parcelable
