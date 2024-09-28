package ru.tensor.sbis.design.gallery.impl.ui.primitives

/**
 * Реализация mvi-сущности Event
 */
internal sealed class GalleryEventMvi {

    /** @SelfDocumented */
    class ItemClicked(val id: Int) : GalleryEventMvi()

    /** @SelfDocumented */
    class AlbumClicked(val id: Int) : GalleryEventMvi()

    /** @SelfDocumented */
    class ItemCheckboxClicked(val id: Int) : GalleryEventMvi()

    /** @SelfDocumented */
    object BackPressed : GalleryEventMvi()

    /** Нажата кнопка "Добавить" */
    object AddButtonClick : GalleryEventMvi()

    /** Нажата кнопка "Отменить" */
    object CancelButtonClick : GalleryEventMvi()

    /** @SelfDocumented */
    object CameraPreviewClick : GalleryEventMvi()

    /** @SelfDocumented */
    object CameraStubClick : GalleryEventMvi()

    /** @SelfDocumented */
    object StorageStubClick : GalleryEventMvi()

    /** @SelfDocumented */
    object MainStubClick : GalleryEventMvi()
}