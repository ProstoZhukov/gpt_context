package ru.tensor.sbis.design.gallery.decl

import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem

/**
 * События, генерируемые компонентом галереи
 */
sealed interface GalleryEvent {

    /** Событие изменения списка выбранных элементов */
    class OnFilesSelected(val selectedFiles: List<SbisPickedItem.LocalFile>) : GalleryEvent

    /**
     * Событие клика по кнопке "Добавить"
     * Кнопки "Добавить" и "Отменить" появляются при [GalleryMode.ByAlbums], переданном в конфиг [GalleryConfig]
     */
    class OnAddButtonClick(val selectedFiles: List<SbisPickedItem.LocalFile>) : GalleryEvent

    /** Событие клика по кнопке "Отменить" */
    class OnCancelButtonClick : GalleryEvent

    /** Событие успешного снимка камеры */
    class OnCameraSnapshotSuccess(val snapshot: SbisPickedItem.LocalFile) : GalleryEvent

    /** Событие о завершении обрезки фото [croppedImage] */
    class OnImageCropped(val croppedImage: SbisPickedItem.LocalFile) : GalleryEvent

    /** Событие успешного распознавания штрих-кода */
    class OnBarcodeScannerResult(val barcode: SbisPickedItem.Barcode) : GalleryEvent
}