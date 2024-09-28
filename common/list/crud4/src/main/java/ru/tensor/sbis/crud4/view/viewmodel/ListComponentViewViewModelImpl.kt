package ru.tensor.sbis.crud4.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.ItemWithSection
import ru.tensor.sbis.crud4.ListComponentViewViewModel
import ru.tensor.sbis.crud4.SectionMapperWrapper
import ru.tensor.sbis.crud4.domain.Reset
import ru.tensor.sbis.crud4.domain.ResetWithSection
import ru.tensor.sbis.crud4.view.SingleLiveEvent
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Реализация вью модели экрана списка на основе crud4 коллекции. Внутри использует [ComponentViewModel] но произваод
 * мапинг элементов с учетом секций
 *
 * @author ma.kolpakov
 */
@AnyThread
internal class ListComponentViewViewModelImpl<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>(
    private val innerVm: ComponentViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
) : ViewModel(),
    ComponentViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER> by innerVm,
    ListComponentViewViewModel<COLLECTION, ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>
    where  SOURCE_ITEM : DecoratedProtocol<IDENTIFIER> {

    private val _scrollToPositionEvent = SingleLiveEvent<Int>()
    private val _scrollEvent = SingleLiveEvent<ScrollEvent>()
    private val _onCleanList = SingleLiveEvent<Unit>()


    override val scrollToPositionEvent: LiveData<Int> = _scrollToPositionEvent
    override val onCleanList: LiveData<Unit> = _onCleanList

    override fun scrollToPosition(position: Int) {
        _scrollToPositionEvent.postValue(position)
    }

    override val onScrollEvent: LiveData<ScrollEvent> = _scrollEvent

    override fun onScroll(recyclerView: RecyclerView, dx: Int, dy: Int) {
        _scrollEvent.postValue(ScrollEvent(dx, dy))
    }

    override fun cleanList() {
        _onCleanList.postValue(Unit)
    }

    override fun reset(reset: ResetWithSection<FILTER, SOURCE_ITEM, AnyItem, IDENTIFIER>) {
        when (reset) {
            is ResetWithSection.MapperWithSection -> {
                reset(Reset.Mapper(SectionMapperWrapper(reset.mapper)))
            }

            is ResetWithSection.FilterAndMapperWithSection -> {
                reset(Reset.FilterAndMapper(reset.filter, SectionMapperWrapper(reset.mapper)))
            }

            is ResetWithSection.Filter -> {
                reset(Reset.Filter(reset.filter))
            }
        }
    }

}

/**
 * Событие покрутки
 */
data class ScrollEvent(val dx: Int, val dy: Int)