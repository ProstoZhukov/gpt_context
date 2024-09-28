package ru.tensor.sbis.crud3.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.crud3.SectionMapperWrapper
import ru.tensor.sbis.crud3.domain.Reset
import ru.tensor.sbis.crud3.domain.ResetWithSection
import ru.tensor.sbis.crud3.view.SingleLiveEvent
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Реализация вью модели экрана списка на основе CRUD3 коллекции. Внутри использует [ComponentViewModel] но произваод
 * мапинг элементов с учетом секций
 *
 * @author ma.kolpakov
 */
@AnyThread
internal class ListComponentViewViewModelImpl<FILTER, SOURCE_ITEM>(
    private val innerVm: ComponentViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM>,
) : ViewModel(), ComponentViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM> by innerVm,
    ListComponentViewViewModel<ItemWithSection<AnyItem>, FILTER, SOURCE_ITEM> {
    private val _scrollToPositionEvent = SingleLiveEvent<Int>()
    private val _scrollEvent = SingleLiveEvent<ScrollEvent>()
    override val scrollToPositionEvent: LiveData<Int> = _scrollToPositionEvent

    override fun scrollToPosition(position: Int) {
        _scrollToPositionEvent.postValue(position)
    }

    override val onScrollEvent: LiveData<ScrollEvent> = _scrollEvent

    override fun onScroll(recyclerView: RecyclerView, dx: Int, dy: Int) {
        _scrollEvent.postValue(ScrollEvent(dx, dy))
    }

    override fun reset(reset: ResetWithSection<FILTER, SOURCE_ITEM, AnyItem>) {
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