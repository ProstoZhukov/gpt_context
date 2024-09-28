package ru.tensor.sbis.crud4.domain

import ru.tensor.sbis.service.DecoratedProtocol

/**
 *  Интерфейс, определяющий данные для [ListComponentViewViewModel.reset]
 *
 * @author ma.kolpakov
 */
sealed interface ResetWithSection<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER> {

    /**
     * Модель содержащая фильтр с маппером
     */
    class FilterAndMapperWithSection<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(
        val filter: FILTER, val mapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
    ) : ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>

    /**
     * Модель содержащая маппер
     */
    class MapperWithSection<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(
        val mapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
    ) :
        ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>

    /**
     * Модель содержащая фильтр
     */
    class Filter<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(val filter: FILTER) :
        ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
}