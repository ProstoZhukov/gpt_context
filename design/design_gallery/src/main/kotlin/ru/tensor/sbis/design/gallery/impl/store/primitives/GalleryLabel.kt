package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.net.Uri
import androidx.annotation.IntRange
import ru.tensor.sbis.design.files_picker.decl.CropParams
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem

/**
 * Реализация mvi-сущности Label
 */
internal sealed interface GalleryLabel {

    /**
     * Показать просмотрщик элементов галереи
     *
     * @property id                 Идентификатор элемента, с которого начнется просмотр элементов
     * @property items              Список элементов для просмотра
     * @property selectedItemsIds   Список идентификаторов выбранных элементов
     * @property selectionLimit     Лимит выбора
     */
    data class ShowViewerSlider(
        val id: Int,
        val items: List<GalleryItem>,
        val selectedItemsIds: List<Int>,
        val selectionLimit: Int
    ) : GalleryLabel

    /**
     * Нажата кнопка "Добавить"
     *
     * @property items Список выбранных элементов
     */
    data class AddButtonClicked(val items: List<GalleryItem>) : GalleryLabel

    /**
     *  Изменился список выбранных элементов
     *
     *  @property items Список выбранных элементов
     */
    data class ItemsSelected(val items: List<GalleryItem>) : GalleryLabel

    /** Достигнут лимит выбора [limit] */
    data class SelectionLimit(@IntRange(from = 1) val limit: Int) : GalleryLabel

    /** Размер файла превышает ограничение [sizeInBytesLimit] */
    data class SizeLimit(val sizeInBytesLimit: Int) : GalleryLabel

    /** Нажата кнопка "Отменить" */
    object CancelButtonClicked : GalleryLabel

    /** Сделан снимок */
    data class SnapshotTaken(val item: SbisPickedItem.LocalFile) : GalleryLabel

    /** Запустить экран обрезки изображения по [uri] с параметрами [cropParams] */
    data class CropImage(val uri: Uri, val cropParams: CropParams) : GalleryLabel

    /** @SelfDocumented */
    data class OnBarcodeScannerResult(val item: SbisPickedItem.Barcode) : GalleryLabel

    /** @SelfDocumented */
    object OpenCamera : GalleryLabel

    /**
     * Проверить существующие разрешения приложения перед показом контента
     * Если нет какого-либо, вызываем диалог
     */
    object CheckPermissions : GalleryLabel

    /** Нажата заглушка камеры, запросить разрешение */
    object CameraStubClicked : GalleryLabel

    /** Нажата заглушка элементов галереи, запросить разрешение */
    object StorageStubClicked : GalleryLabel

    /** Нажата основная заглушка (когда нет никаких разрешений), запросить разрешения */
    object MainStubClicked : GalleryLabel
}