package ru.tensor.sbis.communicator.common.util.message_search

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import java.util.UUID

/**
 * API для поиска сообщений по диалогу.
 *
 * @author dv.baranov
 */
interface ThemeMessageSearchApi {

    /** Задать поисковую строку. */
    fun setSearchMessagesQuery(query: String)

    /** Задать персону для поиска. */
    fun setSearchMessagesPerson(person: PersonSuggestData?)

    /** Подписка на список доступных персон для поиска. */
    val suggestedPersons: Flow<List<PersonSuggestData>>

    /** Подписка на список найденных сообщений. */
    val foundMessages: Flow<List<UUID>>

    /** Подписка на число найденных сообщений. */
    val totalFoundMessagesCount: Flow<Int>
}
