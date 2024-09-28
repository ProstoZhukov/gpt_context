package ru.tensor.sbis.crud3

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import ru.tensor.sbis.crud3.data.Crud3CollectionWrapper
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.Reset

/**
 * Интерфейс для внешнего воздействия на компонент списка.
 */
interface ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM> : ResetableRefreshable<FILTER> {

    @AnyThread
    @Deprecated(message = "Устарел, использовать другую перегрузку")
    fun reset(filter: FILTER? = null, mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>? = null)

    /**
     * Установить новое значение фильтра и/или маппера и отобразить список с начальной позиции.
     * Если ни фильтр ни маппер не будут переданы, то список отобразится с начальной позиции с фильтром и маппером,
     * заданными по умолчанию(см. [Crud3CollectionWrapper.createEmptyFilter]).
     * Разница с методом [ResetableRefreshable.reset] заключается в том, что если в этот метод не будет передан
     * фильтр, то он не будет сброшен до дефолтного и список не отобразиться с начальной позиции.
     */
    @AnyThread
    fun reset(reset: Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM>)

    /**
     * Событие нажатия на элемент списка. Придет на UI потоке.
     */
    val onItemClick: LiveData<SOURCE_ITEM>
}

