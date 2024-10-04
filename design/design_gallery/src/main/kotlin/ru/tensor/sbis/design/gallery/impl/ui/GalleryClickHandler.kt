package ru.tensor.sbis.design.gallery.impl.ui

import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryAlbumItemVM
import ru.tensor.sbis.design.gallery.impl.ui.primitives.GalleryItemVM

/**
 * Обработчик нажатий компонента галереи
 */
internal interface GalleryClickHandler {

    /** @SelfDocumented */
    fun onAlbumItemClick(item: GalleryAlbumItemVM)

    /** @SelfDocumented */
    fun onMediaItemClick(item: GalleryItemVM)

    /** @SelfDocumented */
    fun onCheckboxClick(item: GalleryItemVM)

    /** @SelfDocumented */
    fun onCameraPreviewClick()

    /** @SelfDocumented */
    fun onCameraStubClick()

    /** @SelfDocumented */
    fun onStorageStubClick()
}