package ru.tensor.sbis.design.files_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Раздел пикера.
 *
 * @author ai.abramenko
 */
abstract class SbisFilesPickerTab : Parcelable {

    companion object {

        const val DEFAULT_SELECTION_LIMIT = 25

        /**
         * Получить минимальное количество разделов - только Галерея.
         *
         * @param fileSizeInMBytesLimit  Максимальный размер файла в мегабайтах доступного для выбора.
         * @param selectionMode          Режим выбора файлов: одиночный/множественный.
         */
        fun getGallery(
            fileSizeInMBytesLimit: Int? = null,
            selectionMode: GallerySelectionMode = GallerySelectionMode.Multiple(DEFAULT_SELECTION_LIMIT),
            needOnlyImages: Boolean = false
        ) =
            setOf(Gallery(selectionMode, fileSizeInMBytesLimit, needOnlyImages))

        /**
         * Получить типовое количество разделов.
         * Включает в себя: Галерею, Файлы, Документы и Скан.
         *
         * @param filesSelectionLimit   Максимальное количество доступных для выбора файлов.
         * @param fileSizeInMBytesLimit Максимальный размер файла в мегабайтах доступного для выбора.
         */
        @Suppress("unused")
        fun getStandardSet(
            filesSelectionLimit: Int = DEFAULT_SELECTION_LIMIT,
            fileSizeInMBytesLimit: Int? = null,
            isRecentEnabled: Boolean = true,
            isFavoritesEnabled: Boolean = true,
            isBufferEnabled: Boolean = true,
            isMyDiskEnabled: Boolean = true,
            isCompanyDiskEnabled: Boolean = true
        ) =
            setOf(
                Gallery(GallerySelectionMode.Multiple(filesSelectionLimit), fileSizeInMBytesLimit),
                Files(
                    filesSelectionLimit = filesSelectionLimit,
                    fileSizeInMBytesLimit = fileSizeInMBytesLimit,
                    isRecentEnabled = isRecentEnabled,
                    isFavoritesEnabled = isFavoritesEnabled,
                    isBufferEnabled = isBufferEnabled,
                    isMyDiskEnabled = isMyDiskEnabled,
                    isCompanyDiskEnabled = isCompanyDiskEnabled
                ),
                Scanner(),
                Tasks()
            )
    }

    /**
     * Галерея.
     *
     * @property selectionMode          Режим выбора файлов: одиночный/множественный.
     * @property fileSizeInMBytesLimit  Максимальный размер файла в мегабайтах доступного для выбора.
     * @property needOnlyImages         Требуются ли только фото.
     * @property cameraType             Тип камеры.
     */
    @Parcelize
    class Gallery(
        val selectionMode: GallerySelectionMode = GallerySelectionMode.Multiple(DEFAULT_SELECTION_LIMIT),
        val fileSizeInMBytesLimit: Int? = null,
        val needOnlyImages: Boolean = false,
        val cameraType: GalleryCameraType = GalleryCameraType.Default(),
    ) : SbisFilesPickerTab()

    /**
     * Файлы.
     *
     * @property filesSelectionLimit    Максимальное количество доступных для выбора файлов.
     * @property fileSizeInMBytesLimit  Максимальный размер файла в мегабайтах доступного для выбора.
     * @property cropParams             Параметры обрезки изображения, если не null и [filesSelectionLimit] == 1,
     *                                  то будут учитываться в галерее на вкладке "Файлы".
     * @property isRecentEnabled        Включены ли последнии файлы для выбора.
     * @property isFavoritesEnabled     Включены ли избранные файлы для выбора.
     * @property isBufferEnabled        Включен ли буффер для выбора.
     * @property isMyDiskEnabled        Включен ли Мой диск для выбора.
     * @property isCompanyDiskEnabled   Включен ли Диск компании для выбора.
     * @property isGalleryEnabled       Включена ли галерея для выбора.
     */
    @Parcelize
    class Files(
        val filesSelectionLimit: Int = DEFAULT_SELECTION_LIMIT,
        val fileSizeInMBytesLimit: Int? = null,
        val cropParams: CropParams? = null,
        val isRecentEnabled: Boolean = true,
        val isFavoritesEnabled: Boolean = true,
        val isBufferEnabled: Boolean = true,
        val isMyDiskEnabled: Boolean = true,
        val isCompanyDiskEnabled: Boolean = true,
        val isGalleryEnabled: Boolean = true
    ) : SbisFilesPickerTab() {

        companion object {

            fun onlyStorageFiles(
                filesSelectionLimit: Int = DEFAULT_SELECTION_LIMIT,
                fileSizeInMBytesLimit: Int? = null,
            ): Files =
                Files(
                    filesSelectionLimit = filesSelectionLimit,
                    fileSizeInMBytesLimit = fileSizeInMBytesLimit,
                    isRecentEnabled = false,
                    isFavoritesEnabled = false,
                    isMyDiskEnabled = false,
                    isCompanyDiskEnabled = false,
                    isBufferEnabled = false,
                    isGalleryEnabled = false
                )
        }
    }

    /** Сканер. */
    @Parcelize
    class Scanner : SbisFilesPickerTab()

    /** Задачи. */
    @Parcelize
    class Tasks : SbisFilesPickerTab()
}