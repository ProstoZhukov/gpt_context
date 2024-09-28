package ru.tensor.sbis.crud4.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.closeItOnDestroyVm
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.crud4.CollectionViewModel
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.ListComponent
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.Reset
import ru.tensor.sbis.crud4.view.DefaultMapStrategy
import ru.tensor.sbis.crud4.view.SingleLiveEvent
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.SetItems
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Реализация вью модели крана списка на основе crud4 коллекции. Удерживает данные при смене конфигурации приложения.
 * Содержит в себе недостающую в контроллере логику работы с пагинацией, отображением индикаторов пагинации по месту.
 * Связывает события контроллера по отображению заглушки с реализациями заглушек, предоставленными пользователем
 * компонента, а так же реагирует на пулл-ту-рефреш.
 */
@AnyThread
internal class ComponentViewModelImpl<COLLECTION, FILTER, SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM : Any, PATH_MODEL, IDENTIFIER>(
    itemMapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>,
    private val innerVm: CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    private val _onItemClick: SingleLiveEvent<SOURCE_ITEM> = SingleLiveEvent(),
    private val _onItemClickFlow: MutableSharedFlow<SOURCE_ITEM> = MutableSharedFlow(),
    private var mapStrategy: DefaultMapStrategy<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> = DefaultMapStrategy(itemMapper)
) : ViewModel(), CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> by innerVm,
    ComponentViewModel<COLLECTION, OUTPUT_ITEM, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    ListComponent<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> {

    private val _onMove = SingleLiveEvent<PATH_MODEL?>()
    private val _onOpenFolder = SingleLiveEvent<Pair<IDENTIFIER, IDENTIFIER?>>()
    private val _goBackFolderEvent = SingleLiveEvent<Unit>()

    override val onMove: LiveData<PATH_MODEL?> = _onMove

    override val onOpenFolder: LiveData<Pair<IDENTIFIER, IDENTIFIER?>> = _onOpenFolder
    override val onGoBackFolder: LiveData<Unit> = _goBackFolderEvent

    private val setItemsEvent = PublishSubject.create<DataChange<SOURCE_ITEM>>()
    private var lastSourceItems = listOf<SOURCE_ITEM>()
    private val _dataChangeMapped: PublishSubject<DataChange<OUTPUT_ITEM>> = PublishSubject.create()

    @Deprecated("Переход на flow", replaceWith = ReplaceWith("onItemClickFlow"))
    override val onItemClick: LiveData<SOURCE_ITEM> = _onItemClick
    override val onItemClickFlow: SharedFlow<SOURCE_ITEM> = _onItemClickFlow
    override val scrollScrollToZeroPosition = innerVm.scrollScrollToZeroPosition
    override val dataChangeMapped: Observable<DataChange<OUTPUT_ITEM>> = _dataChangeMapped.startWith(
        Observable.fromCallable { mapStrategy.map(SetItems(lastSourceItems), defaultClickAction) }
    )

    private val defaultClickAction = ItemActionDelegateImpl(this)

    init {
        Observable.merge(
            innerVm.dataChange,
            setItemsEvent
        ).subscribe { action ->
            lastSourceItems = action.allItems
            _dataChangeMapped.onNext(mapStrategy.map(action, defaultClickAction))
        }.also {
            it.closeItOnDestroyVm(this)
        }
    }


    override fun openFolder(view: IDENTIFIER, folder: IDENTIFIER) {
        _onOpenFolder.postValue(Pair(view, folder))
    }

    override fun openFolder(pathModel: PATH_MODEL) {
        _onMove.postValue(pathModel)
    }

    override fun goBackFolder() {
        _goBackFolderEvent.postValue(Unit)
    }

    override fun refresh() {
        innerVm.refresh()
    }

    override fun reset(filter: FILTER?) {
        innerVm.reset(filter)
    }

    override fun resetForce(filter: FILTER?) {
        innerVm.resetForce(filter)
    }

    @Deprecated(message = "Устарел, использовать другую перегрузку")
    override fun reset(filter: FILTER?, mapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>?) {
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

    override fun reset(reset: Reset<FILTER, SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>) {
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

    class ItemActionDelegateImpl<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(private val componentViewModelImpl: ComponentViewModelImpl<*, *, SOURCE_ITEM, *, *, IDENTIFIER>) :
        ItemActionDelegate<SOURCE_ITEM, IDENTIFIER> {
        override fun itemClick(item: SOURCE_ITEM) {
            componentViewModelImpl.run {
                _onItemClick.postValue(item)
                viewModelScope.launch(Dispatchers.Main) {
                    _onItemClickFlow.emit(item)
                }
            }
        }

        override fun expandFolderClick(item: SOURCE_ITEM) {
            if (item.isExpanded) {
                componentViewModelImpl.collapse(item)
            } else {
                componentViewModelImpl.expand(item)
            }
        }

        override fun expandFolderClick(item: Long) {
            componentViewModelImpl.expand(item)
        }

        override fun openFolderClick(item: SOURCE_ITEM) =
            componentViewModelImpl.openFolder(item.origin!!.getIdentifier(), item.origin!!.getIdentifier())

        override fun selectClick(item: SOURCE_ITEM) = componentViewModelImpl.select(item)
    }
}
