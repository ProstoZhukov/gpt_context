package ru.tensor.sbis.info_decl.knowledge_ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Конфигурация раздела "База знаний".
 *
 * @author am.boldinov
 */
@Parcelize
data class KnowledgeBaseRegistryConfiguration(
    val content: KnowledgeBaseContent = KnowledgeBaseContent.Wiki
) : Parcelable

/**
 * Тип содержимого для отображения в базе знаний.
 */
sealed interface KnowledgeBaseContent : Parcelable {
    /**
     * Статьи.
     */
    @Parcelize
    object Wiki : KnowledgeBaseContent

    /**
     * Инструкции.
     */
    @Parcelize
    object Instructions : KnowledgeBaseContent
}