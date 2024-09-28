package ru.tensor.sbis.design.selection.ui.list

import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.disposables.Disposables
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode.PREFETCH
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.View

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionListInteractorImplTest {

    @Mock
    private lateinit var selection: List<SelectorItemModel>

    @Mock
    private lateinit var entity: SelectionListScreenEntity<Any, Any, Any>

    @Mock
    private lateinit var view: View<SelectionListScreenEntity<Any, Any, Any>>

    @Mock
    private lateinit var interactorDelegate: ListInteractor<SelectionListScreenEntity<Any, Any, Any>>

    private lateinit var interactor: SelectionListInteractorImpl<Any, Any, Any>

    @Before
    fun setUp() {
        interactor = SelectionListInteractorImpl(interactorDelegate, mock())
    }

    @Test
    fun `When selection is applied for entity, then in should receive selected items list`() {
        interactor.applySelection(selection, entity, view)

        verify(entity).setSelection(selection)
    }

    @Test
    fun `Given entity, when selection is applied for it, then view should show data`() {
        interactor.applySelection(selection, entity, view)

        verify(view, only()).showData(entity)
    }

    @Test
    fun `Given stub entity, when selection is applied for it, then view should show stub`() {
        whenever(entity.isStub()).thenReturn(true)
        whenever(selection.size).thenReturn(1)

        interactor.applySelection(selection, entity, view)

        verify(view, only()).showStub(entity, true)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=52b7f7fa-f228-4176-9076-89533b73c714
    @Test
    fun `Given entity need to prefetch and it has next page, when selection is applied for it, then interactor delegate should start loading next items`() {
        whenever(entity.needToPrefetch()).thenReturn(PREFETCH)
        whenever(entity.hasNext()).thenReturn(true)
        whenever(selection.size).thenReturn(1)
        whenever(interactorDelegate.nextPage(entity, view)).thenReturn(Disposables.empty())

        interactor.applySelection(selection, entity, view)

        verify(interactorDelegate, only()).nextPage(entity, view)
    }

    @Test
    fun `Given entity need to prefetch and it doesn't have next page, when selection is applied for it, then interactor delegate should start loading previous items`() {
        whenever(entity.needToPrefetch()).thenReturn(PREFETCH)
        whenever(entity.hasNext()).thenReturn(false)
        whenever(entity.hasPrevious()).thenReturn(true)
        whenever(selection.size).thenReturn(1)
        whenever(interactorDelegate.previousPage(entity, view)).thenReturn(Disposables.empty())

        interactor.applySelection(selection, entity, view)

        verify(interactorDelegate, only()).previousPage(entity, view)
    }
    //endregion

    @Test
    fun `Given entity need to prefetch, but it doesn't have any to prefetch, when selection is applied for it, then view should show data`() {
        whenever(entity.needToPrefetch()).thenReturn(PREFETCH)
        whenever(entity.hasNext()).thenReturn(false)
        whenever(entity.hasPrevious()).thenReturn(false)
        whenever(selection.size).thenReturn(1)

        interactor.applySelection(selection, entity, view)

        verify(view, only()).showData(entity)
    }
}
