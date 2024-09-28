package ru.tensor.sbis.design.person_suggest.input.contract

import ru.tensor.sbis.design.person_suggest.input.PersonInputLayout
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData

/**
 * API контейнера поисковой строки с фильтром по персоне [PersonInputLayout].
 *
 * @author vv.chekurda
 */
interface PersonInputLayoutApi {

    /**
     * Установить/получить слушателя контейнера [PersonInputLayoutListener].
     */
    var listener: PersonInputLayoutListener?

    /**
     * Установить/получить фильтр по персоне.
     */
    var personFilter: PersonSuggestData?

    /**
     * Очистить фильтр по персоне.
     */
    fun clearPersonFilter()
}