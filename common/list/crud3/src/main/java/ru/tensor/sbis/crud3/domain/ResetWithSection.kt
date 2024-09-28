package ru.tensor.sbis.crud3.domain

/**
 *  Интерфейс, определяющий данные для [ListComponentViewViewModel.reset]
 *
 * @author ma.kolpakov
 */
sealed interface ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM> {

    /**
     * Модель содержащая фильтр с маппером
     */
    class FilterAndMapperWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(
        val filter: FILTER, val mapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM>
    ) : ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM>

    /**
     * Модель содержащая маппер
     */
    class MapperWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(val mapper: ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM>) :
        ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM>

    /**
     * Модель содержащая фильтр
     */
    class Filter<FILTER, SOURCE_ITEM, OUTPUT_ITEM>(val filter: FILTER) :
        ResetWithSection<FILTER, SOURCE_ITEM, OUTPUT_ITEM>
}