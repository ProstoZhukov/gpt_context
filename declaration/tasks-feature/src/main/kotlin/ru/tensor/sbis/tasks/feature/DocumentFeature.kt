package ru.tensor.sbis.tasks.feature

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет внешний API модуля документ.
 *
 * @author aa.sviridov
 */
interface DocumentFeature : Feature {

    /**
     * Создаёт фрагмент карточки документа.
     * @param args аргументы для чтения карточки документа, см. [DocumentOpenArgs].
     * @param additionalArgs дополнительные аргументы, см. [AdditionalDocumentOpenArgs].
     *
     * @return новый экземпляр фрагмента карточки документа, см. [Fragment].
     */
    fun createDocumentCardFragment(
        args: DocumentOpenArgs,
        additionalArgs: AdditionalDocumentOpenArgs,
    ): Fragment

    /**
     * Создаёт экземпляр [Intent] для запуска фрагмента карточки документа в активности.
     * @param context контекст для создания активности.
     * @param args аргументы для чтения карточки документа, см. [DocumentOpenArgs].
     * @param additionalArgs д
     * @return новый экземпляр [Intent]  для запуска активности карточки документа.
     */
    fun createDocumentCardActivityIntent(
        context: Context,
        args: DocumentOpenArgs,
        additionalArgs: AdditionalDocumentOpenArgs,
    ): Intent
}