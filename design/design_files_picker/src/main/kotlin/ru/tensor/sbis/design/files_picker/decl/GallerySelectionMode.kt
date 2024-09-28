package ru.tensor.sbis.design.files_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Режим выбора в галерее.
 *
 * @author ia.nikitin
 */
sealed interface GallerySelectionMode : Parcelable {

    /**
     * Режим одиночного выбора.
     *
     * @property imageCropParams Параметры обрезки изображения.
     *
     * @author ia.nikitin
     */
    @Parcelize
    class Single(val imageCropParams: CropParams? = null) : GallerySelectionMode

    /**
     * Режим множественного выбора. Не поддерживает обрезку изображений.
     *
     * @property selectionLimit Максимальное число файлов, которые можно выбрать.
     *
     * @author ia.nikitin
     */
    @Parcelize
    class Multiple(val selectionLimit: Int) : GallerySelectionMode
}