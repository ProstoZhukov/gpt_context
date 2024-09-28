package ru.tensor.sbis.crud3.view

import androidx.annotation.AnyThread
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.view.datachange.DataChange

/**
 * Маппер события обновления данных адаптера списка с установкой события нажатия на элемент.
 */
internal class DefaultMapStrategy<SOURCE_ITEM, OUTPUT_ITEM : Any>(
    var itemMapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>,
    private val _onItemClick: SingleLiveEvent<SOURCE_ITEM>,
    private var actionMapper: DataChangeMapper<SOURCE_ITEM, OUTPUT_ITEM> = DataChangeMapper()
) {
    /**
     * Выполнить маппиинг события.
     */
    @AnyThread
    fun map(action: DataChange<SOURCE_ITEM>) = actionMapper.map(
        action,
        mapItem = { item ->
            itemMapper.map(item) { _onItemClick.postValue(it!!) }
        }
    )
}