package ru.tensor.sbis.crud3.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.closeItOnDestroyVm
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.crud3.CollectionViewModel
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.Reset
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.DefaultMapStrategy
import ru.tensor.sbis.crud3.view.datachange.SetItems
import ru.tensor.sbis.crud3.view.SingleLiveEvent

/**
 * Реализация вью модели крана списка на основе CRUD3 коллекции. Удерживает данные при смене конфигурации приложения.
 * Содержит в себе недостающую в контроллере логику работы с пагинацией, отображением индикаторов пагинации по месту.
 * Связывает события контроллера по отображению заглушки с реализациями заглушек, предоставленными пользователем
 * компонента, а так же реагирует на пулл-ту-рефреш.
 */
@AnyThread
internal class ComponentViewModelImpl<FILTER, SOURCE_ITEM, OUTPUT_ITEM : Any>(
    itemMapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>,
    private val innerVm: CollectionViewModel<FILTER, SOURCE_ITEM>,
    private val _onItemClick: SingleLiveEvent<SOURCE_ITEM> = SingleLiveEvent(),
    private var mapStrategy: DefaultMapStrategy<SOURCE_ITEM, OUTPUT_ITEM> = DefaultMapStrategy(itemMapper, _onItemClick)
) : ViewModel(), CollectionViewModel<FILTER, SOURCE_ITEM> by innerVm,
    ComponentViewModel<OUTPUT_ITEM, FILTER, SOURCE_ITEM>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM> {

    private val setItemsEvent = PublishSubject.create<DataChange<SOURCE_ITEM>>()
    private var lastSourceItems = listOf<SOURCE_ITEM>()
    private val _dataChangeMapped: PublishSubject<DataChange<OUTPUT_ITEM>> = PublishSubject.create()
    override val onItemClick: LiveData<SOURCE_ITEM> = _onItemClick
    override val scrollScrollToZeroPosition = innerVm.scrollScrollToZeroPosition
    override val dataChangeMapped: Observable<DataChange<OUTPUT_ITEM>> = _dataChangeMapped.startWith(
        Observable.fromCallable { mapStrategy.map(SetItems(lastSourceItems)) }
    )

    init {
        Observable.merge(
            innerVm.dataChange,
            setItemsEvent
        ).subscribe { action ->
            lastSourceItems = action.allItems
            _dataChangeMapped.onNext(mapStrategy.map(action))
        }.also {
            it.closeItOnDestroyVm(this)
        }
    }

    override fun refresh() {
        innerVm.refresh()
    }

    override fun reset(filter: FILTER?) {
        return innerVm.reset(filter)
    }

    override fun reset(filter: FILTER?, mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM>?) {
        var newMapper = false
        if (mapper != null && mapper != this.mapStrategy.itemMapper) {
            newMapper = true
            this.mapStrategy.itemMapper = mapper
        }

        /** Если фильтр не менялся, то не произойдет поступление сигнала о показе новых данных, поэтому маппим
         * текущие элементы и зовем событие сами.*/
        if (filter == null) {
            if (newMapper) remapItems()
            return
        }
        /** Фильтр поменялся, передаем его дальше и сигнал о показе новых данных придет.*/
        innerVm.reset(filter)
    }

    override fun reset(reset: Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM>) {
        when (reset) {
            is Reset.Mapper -> {
                if (reset.mapper != this.mapStrategy.itemMapper) {
                    this.mapStrategy.itemMapper = reset.mapper
                    remapItems()
                }
            }

            is Reset.FilterAndMapper -> {
                if (reset.mapper != this.mapStrategy.itemMapper) {
                    this.mapStrategy.itemMapper = reset.mapper
                    remapItems()
                }
                innerVm.reset(reset.filter)
            }

            is Reset.Filter -> {
                innerVm.reset(reset.filter)
            }
        }
    }

    private fun remapItems() {
        setItemsEvent.onNext(SetItems(lastSourceItems))
    }
}