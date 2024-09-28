package ru.tensor.sbis.design.person_suggest.input.contract

import java.util.UUID

/**
 * Слушатель контейнера поисковой строки с фильтром персоны.
 *
 * @author vv.chekurda
 */
interface PersonInputLayoutListener {

    /**
     * Клик по персоне в фильтре с идентификатором [personUuid].
     */
    fun onPersonClick(personUuid: UUID) = Unit

    /**
     * Клик по кнопке закрытия фильтра по персоне.
     */
    fun onCancelPersonFilterClick() = Unit
}