package ru.tensor.sbis.communication_decl.conversation_information

/**
 * Интерфейс для разделов экрана информации диалога/канала, которые поддерживают фильтрацию.
 *
 * @author dv.baranov
 */
interface ConversationInformationFilterableContent {

    /** Установить фильтр. */
    fun setFilter(filter: List<ConversationInformationFilter>)
}