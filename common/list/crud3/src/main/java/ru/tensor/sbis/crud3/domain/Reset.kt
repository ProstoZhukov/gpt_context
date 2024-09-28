package ru.tensor.sbis.crud3.domain

/**
 *  Интерфейс, определяющий данные для [ListComponent.reset]
 *
 * @author du.bykov
 */
sealed interface Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM> {

    /**
     * Модель содержащая фильтр с маппером
     */
    class FilterAndMapper<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(
        val filter: FILTER, val mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>
    ) : Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM>

    /**
     * Модель содержащая маппер
     */
    class Mapper<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(val mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>) :
        Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM>

    /**
     * Модель содержащая фильтр
     */
    class Filter<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(val filter: FILTER) : Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM>
}