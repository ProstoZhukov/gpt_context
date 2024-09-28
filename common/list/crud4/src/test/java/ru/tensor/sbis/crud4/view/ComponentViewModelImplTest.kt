package ru.tensor.sbis.crud4.view

import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
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
import ru.tensor.sbis.crud4.CollectionViewModel
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.Reset
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.SetItems
import ru.tensor.sbis.crud4.view.viewmodel.ComponentViewModelImpl
import ru.tensor.sbis.list.view.DataChangedObserver
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.IdentifierProtocol
import ru.tensor.sbis.service.generated.MoreButton
import ru.tensor.sbis.service.generated.SelectionStatus

internal class ComponentViewModelImplTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val itemMapper = mock<ItemMapper<TestItem, String, Any>>()
    private val publishSubject = PublishSubject.create<DataChange<TestItem>>()
    private val innerVm = mock<CollectionViewModel<Any, Any, TestItem, Any, Any>> {
        on { dataChange } doReturn publishSubject
    }
    private val dataChange: DataChange<TestItem> = mock<SetItems<TestItem>>()
    private val componentMapper = mock<DefaultMapStrategy<TestItem, String, Any>> {
        on { map(any(), any()) } doReturn mock<SetItems<String>>()
    }
    private lateinit var viewModel: ComponentViewModelImpl<Any, Any, TestItem, String, Any, Any>

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

        assertEquals(componentMapper.map(dataChange, mock()), test.events[0][0])
    }
    @Ignore("https://online.sbis.ru/opendoc.html?guid=e49830e1-aeeb-4535-b9a8-c126aab575de&client=3")
    @Test
    fun resetFilter() {
        viewModel.reset()

        verify(innerVm).reset()
    }
    @Ignore ("https://online.sbis.ru/opendoc.html?guid=e49830e1-aeeb-4535-b9a8-c126aab575de&client=3")
    @Test
    fun `When reset with no filter then do not reset to inner Vm`() {
        viewModel.reset(mock<Reset.Mapper<Any, DecoratedProtocol<Any>, Any, Any>>())

        verify(innerVm, never()).reset()
    }

    @Test
    fun `When reset with filter and mapper then do reset to inner Vm`() {
        val filter = mock<Any>()
        val mapper = mock<ItemMapper<TestItem, String, Any>>()
        viewModel.reset(Reset.FilterAndMapper(filter, mapper))

        verify(innerVm).reset(filter)
    }

    @Test
    fun `When reset with filter and mapper then do reset to inner Vm2`() {
        val filter = mock<Any>()
        val mapper = mock<ItemMapper<TestItem, String, Any>>()
        viewModel.reset(Reset.FilterAndMapper(filter, mapper))

        verify(componentMapper).itemMapper = mapper
    }

    @Test
    fun `When reset without filter but with mapper then send SetItems with remapped`() {
        val test = viewModel.dataChangeMapped.test()
        val mockList = mock<List<TestItem>>()
        val dataChange: DataChange<TestItem> = mock<SetItems<TestItem>> {
            on { allItems } doReturn mockList
        }
        val mappedAction: DataChange<String> = mock<SetItems<String>>()
        whenever(componentMapper.map(argThat {
            allItems === mockList
        }, any())) doReturn mappedAction
        publishSubject.onNext(dataChange)

        val mapper = mock<ItemMapper<TestItem, String, Any>>()
        viewModel.reset(null, mapper)

        assertEquals(componentMapper.map(dataChange, mock()), test.events[0][1])
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

class TestItem : DecoratedProtocol<Any> {
    override var isExpanded: Boolean = false

    override var isMarked: Boolean = false

    override var isSelected: SelectionStatus = SelectionStatus.UNSET

    override var level: Long = 1L

    override var nodeType: Boolean? = false

    override val origin: IdentifierProtocol<Any>? = null

    override var stub: MoreButton? = null

    override var subLevel: Boolean = false
}