package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Дополнительные аргументы для открытия карточки документа.
 *
 *  @author aa.sviridov
 */
sealed class AdditionalDocumentOpenArgs : Parcelable {

    /**
     * Дополнительные аргументы для открытия карточки как УПФ.
     * @property printingFormPath путь до файла с упрощённой печатной формой, опционально.
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class PrintingForm(
        val printingFormPath: String,
    ) : AdditionalDocumentOpenArgs()

    /**
     * Дополнительные аргументы для открытия карточки.
     * @property sourceSetKey ключ для выбора набора зависимостей источника данных.
     * @property isPublishedInstruction признак открытия карточки опубликованной инструкции.
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class Regular(
        val sourceSetKey: SourceSetKey = TasksSourceSetKey,
        val isPublishedInstruction: Boolean = false,
    ) : AdditionalDocumentOpenArgs()
}