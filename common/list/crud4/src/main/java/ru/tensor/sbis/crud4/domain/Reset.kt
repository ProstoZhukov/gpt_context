package ru.tensor.sbis.crud4.domain

import ru.tensor.sbis.service.DecoratedProtocol

/**
 *  Интерфейс, определяющий данные для [ListComponent.reset]
 *
 * @author du.bykov
 */
sealed interface Reset<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER> {

    /**
     * Модель содержащая фильтр с маппером
     */
    class FilterAndMapper<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(
        val filter: FILTER, val mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
    ) : Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>

    /**
     * Модель содержащая маппер
     */
    class Mapper<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(
        val mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
    ) :
        Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>

    /**
     * Модель содержащая фильтр
     */
    class Filter<FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER>(val filter: FILTER) :
        Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>
}