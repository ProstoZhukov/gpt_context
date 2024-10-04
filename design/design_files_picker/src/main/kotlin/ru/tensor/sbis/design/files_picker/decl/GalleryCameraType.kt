package ru.tensor.sbis.design.files_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип камеры в галерее. Влияет на получаемый в месте использования результат [SbisPickedItem]
 *
 * @author ia.nikitin
 */
sealed interface GalleryCameraType : Parcelable {

    /** Стандартная камера, возвращает стандартные файлы (фото и видео) [SbisPickedItem.LocalFile] */
    @Parcelize
    class Default : GalleryCameraType

    /**
     * Сканер штрих-кодов, возвращает [SbisPickedItem.Barcode] данные о штрих-коде, если он был найден,
     * и [SbisPickedItem.LocalFile] иначе
     */
    @Parcelize
    class BarcodeScanner : GalleryCameraType
}