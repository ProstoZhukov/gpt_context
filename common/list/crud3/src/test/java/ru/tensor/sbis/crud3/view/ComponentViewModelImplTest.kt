package ru.tensor.sbis.crud3.view

import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.crud3.CollectionViewModel
import ru.tensor.sbis.crud3.domain.ItemMapper
import ru.tensor.sbis.crud3.domain.Reset
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.datachange.SetItems
import ru.tensor.sbis.crud3.view.viewmodel.ComponentViewModelImpl
import ru.tensor.sbis.list.view.DataChangedObserver

internal class ComponentViewModelImplTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val itemMapper = mock<ItemMapper<Float, String>>()
    private val publishSubject = PublishSubject.create<DataChange<Float>>()
    private val innerVm = mock<CollectionViewModel<Any, Float>> {
        on { dataChange } doReturn publishSubject
    }
    private val dataChange: DataChange<Float> = mock<SetItems<Float>>()
    private val componentMapper = mock<DefaultMapStrategy<Float, String>> {
        on { map(any()) } doReturn mock<SetItems<String>>()
    }
    private lateinit var viewModel: ComponentViewModelImpl<Any, Float, String>

    @Before
    fun before() {
        viewModel = ComponentViewModelImpl(
            itemMapper,
            innerVm,
            mapStrategy = componentMapper
        )
    }

    @Test
    fun dataChange() {
        val test = viewModel.dataChangeMapped.test()

        publishSubject.onNext(dataChange)

        assertEquals(componentMapper.map(dataChange), test.events[0][0])
    }

    @Test
    fun resetFilter() {
        viewModel.reset()

        verify(innerVm).reset()
    }

    @Test
    fun `When reset with no filter then do not reset to inner Vm`() {
        viewModel.reset(mock<Reset.Mapper<Any, Any, Any>>())

        verify(innerVm, never()).reset()
    }

    @Test
    fun `When reset with filter and mapper then do reset to inner Vm`() {
        val filter = mock<Any>()
        val mapper = mock<ItemMapper<Float, String>>()
        viewModel.reset(Reset.FilterAndMapper(filter, mapper))

        verify(innerVm).reset(filter)
    }

    @Test
    fun `When reset with filter and mapper then do reset to inner Vm2`() {
        val filter = mock<Any>()
        val mapper = mock<ItemMapper<Float, String>>()
        viewModel.reset(Reset.FilterAndMapper(filter, mapper))

        verify(componentMapper).itemMapper = mapper
    }

    @Test
    fun `When reset without filter but with mapper then send SetItems with remapped`() {
        val test = viewModel.dataChangeMapped.test()
        val mockList = mock<List<Float>>()
        val dataChange: DataChange<Float> = mock<SetItems<Float>> {
            on { allItems } doReturn mockList
        }
        val mappedAction: DataChange<String> = mock<SetItems<String>>()
        whenever(componentMapper.map(argThat {
            allItems === mockList
        })) doReturn mappedAction
        publishSubject.onNext(dataChange)

        val mapper = mock<ItemMapper<Float, String>>()
        viewModel.reset(null, mapper)

        assertEquals(componentMapper.map(dataChange), test.events[0][1])
    }

    @Test
    fun refresh() {
        viewModel.refresh()

        verify(innerVm).refresh()
    }

    @Test
    fun loadNext() {
        viewModel.loadNext()

        verify(innerVm).loadNext()
    }

    @Test
    fun loadPrevious() {
        viewModel.loadPrevious()

        verify(innerVm).loadPrevious()
    }

    @Test
    fun onItemRangeInserted() {
        val provider = mock<DataChangedObserver.ItemVisibilityPositionProvider>()
        viewModel.onItemRangeInserted(11, 22, provider)

        verify(innerVm).onItemRangeInserted(11, 22, provider)
    }
}