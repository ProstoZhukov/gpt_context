package ru.tensor.sbis.communication_decl.conversation_information

/**
 * Интерфейс для разделов экрана информации диалога/канала, которые поддерживают строку поиска.
 *
 * @author dv.baranov
 */
interface ConversationInformationSearchableContent {

    /** Задать поисковый запрос. */
    fun setSearchQuery(query: String)
}