package ru.tensor.sbis.design.selection.bl.vm.selection.multi

import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.vm.DEFAULT_SELECTION_LIMIT
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.AbstractSelectionModeHandler
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.SelectionModeHandlerFactory
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory

/**
 * Тест сценариев [MultiSelectionViewModelImpl] с начальными данными
 *
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MultiSelectionViewModelImplInitialisationTest {

    @Mock
    private lateinit var initialDataA: TestData

    @Mock
    private lateinit var initialDataB: TestData

    @Mock
    private lateinit var selectionLoader: MultiSelectionLoader<TestData>

    @Mock
    private lateinit var metaFactory: ItemMetaFactory

    @Mock
    private lateinit var modeHandler: AbstractSelectionModeHandler<TestData>

    @Mock
    private lateinit var modeHandlerFactory: SelectionModeHandlerFactory<TestData>

    @Mock
    private lateinit var doneButtonViewModel: DoneButtonViewModel

    private lateinit var initialList: List<TestData>
    private lateinit var vm: MultiSelectionViewModelImpl<TestData>
    private lateinit var selection: TestObserver<List<TestData>>
    private lateinit var result: TestObserver<List<TestData>>

    @Before
    fun setUp() {
        initialList = listOf(initialDataA, initialDataB)
        whenever(selectionLoader.loadSelectedItems()).thenReturn(initialList)

        whenever(modeHandler.limitObservable).thenReturn(Observable.never())
        whenever(modeHandlerFactory.createSelectionHandler(any(), any(), any(), eq(DEFAULT_SELECTION_LIMIT)))
            .thenReturn(modeHandler)

        vm = MultiSelectionViewModelImpl(
            selectionLoader,
            metaFactory,
            DEFAULT_SELECTION_LIMIT,
            modeHandlerFactory,
            doneButtonViewModel,
            observeOn = Schedulers.single()
        )
        selection = vm.selection.test().awaitCount(1)
        result = vm.result.test()
    }

    @Test
    fun `When vm initialised with initial data, then it should be delivered to subscribers`() {
        selection.assertValue(listOf(initialDataA, initialDataB))
    }

    @Test
    fun `When view model does not have subscriptions, then selection loader shouldn't be invoked`() {
        verifyNoMoreInteractions(selectionLoader)
    }

    @Test
    fun `When vm get observers, then initial selection should be loaded once`() {
        selection.assertValueCount(1)
        result.assertNoValues()
        verify(selectionLoader, only()).loadSelectedItems()
    }

    @Test
    fun `When initial data loaded, then completion rule should be invoked`() {
        result.awaitCount(1).assertNoValues()
        verify(doneButtonViewModel).setInitialData(initialList)
        verify(doneButtonViewModel).setSelectedData(initialList)
    }

    @Test
    fun `When view model initialised, then initial data should be selected`() {
        selection.assertValueCount(1)

        verify(metaFactory).attachSelectedItemMeta(initialDataA)
        verify(metaFactory).attachSelectedItemMeta(initialDataB)
    }
}