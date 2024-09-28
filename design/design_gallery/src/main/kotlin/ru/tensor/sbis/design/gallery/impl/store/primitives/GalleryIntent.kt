package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.net.Uri
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeSymbology

/**
 * Реализация mvi-сущности Intent
 */
internal sealed interface GalleryIntent {

    /**
     * Результат выбора в просмотрщике
     *
     * @property ids Идентификаторы выбранных элементов
     */
    data class CloseViewer(val ids: List<Int>) : GalleryIntent

    /** @SelfDocumented */
    data class AlbumClicked(val id: Int) : GalleryIntent

    /** @SelfDocumented */
    data class ItemCheckboxClicked(val id: Int) : GalleryIntent

    /** @SelfDocumented */
    data class ItemClicked(val id: Int) : GalleryIntent

    /**
     *  Пользователь подтвердил свой выбор
     *
     *  @property itemsIds Список идентификаторов элементов
     */
    data class SelectionConfirmed(val itemsIds: List<Int>? = null) : GalleryIntent

    /** Обновить uri для снимка с камеры */
    data class UpdateCameraSnapshotUri(val uri: Uri?) : GalleryIntent

    /**
     * Сканер штрих-кода вернул результат, файл и данные о штрих-коде или без них
     *
     * @property barcodeValue       Строковое значение штрих-кода
     * @property barcodeSymbology   Тип штрих-кода
     * @property fileUri            Uri файла
     */
    data class OnBarcodeScannerResult(
        val barcodeValue: String?,
        val barcodeSymbology: BarcodeSymbology?,
        val fileUri: String
    ) : GalleryIntent

    /** Первоначальная загрузка элементов галереи */
    object LoadItems : GalleryIntent

    /** Не предоставлено разрешение на доступ к галерее */
    object StoragePermissionDenied : GalleryIntent

    /** Не предоставлено разрешение на использование камеры */
    object CameraPermissionDenied : GalleryIntent

    /** Ни одно из требуемых разрешений не предоставлено */
    object AllPermissionsDenied : GalleryIntent

    /** @SelfDocumented */
    object BackPressed : GalleryIntent

    /** Нажата кнопка "Отменить" */
    object CancelButtonClicked : GalleryIntent

    /** @SelfDocumented */
    object OpenCamera : GalleryIntent

    /** Пользователь сделал фото */
    object SnapshotTaken : GalleryIntent

    /** @SelfDocumented */
    object RequestCameraPermission : GalleryIntent

    /** @SelfDocumented */
    object RequestStoragePermission : GalleryIntent

    /** @SelfDocumented */
    object RequestPermissions : GalleryIntent

    /** Очистить выбор */
    object ClearSelection : GalleryIntent

    /** Нажата кнопка "Добавить" хост фрагмента FilesPicker */
    object FilesPickerAddButtonClicked : GalleryIntent
}