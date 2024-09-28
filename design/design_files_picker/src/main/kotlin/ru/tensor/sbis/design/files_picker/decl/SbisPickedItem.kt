package ru.tensor.sbis.design.files_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.barcode_decl.barcodereader.Barcode as BarcodeData

/**
 * Выбираемый элемент пикера.
 *
 * @author ai.abramenko
 */
sealed class SbisPickedItem : Parcelable {

    /**
     * Файл с устройства.
     */
    @Parcelize
    class LocalFile(val uri: String) : SbisPickedItem()

    /**
     * Документ со СБИС Диска.
     */
    @Parcelize
    class DiskDocument(val params: DiskDocumentParams) : SbisPickedItem()

    /** Ссылка, например, на задачу. */
    @Parcelize
    class Uri(val url: String) : SbisPickedItem()

    /** Фото штрих-кода с данными о нем. */
    @Parcelize
    class Barcode(val barcode: BarcodeData, val uri: String) : SbisPickedItem()
}