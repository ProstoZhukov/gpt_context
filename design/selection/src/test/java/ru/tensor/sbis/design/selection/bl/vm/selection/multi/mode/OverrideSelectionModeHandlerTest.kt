package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import org.mockito.kotlin.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.DEFAULT_SELECTION_LIMIT
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.filterSelectedItems
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class OverrideSelectionModeHandlerTest {

    /**
     * Публикация событий блокировки потока с выбранными элементами при вызове [completeFunction]
     *
     * @see filterSelectedItems
     */
    private val completeSubject = PublishSubject.create<Any>()

    @Mock
    private lateinit var initialMeta: SelectorItemMeta

    @Mock
    private lateinit var initialData: TestData

    @Mock
    private lateinit var metaA: SelectorItemMeta

    @Mock
    private lateinit var dataA: TestData

    @Mock
    private lateinit var metaB: SelectorItemMeta

    @Mock
    private lateinit var dataB: TestData

    @Mock
    private lateinit var completeFunction: (TestData) -> Unit

    private lateinit var modeHandler: OverrideSelectionModeHandler<TestData>
    private lateinit var selection: TestObserver<List<TestData>>
    private lateinit var limit: TestObserver<Int>

    @Before
    fun setUp() {
        whenever(initialData.meta).thenReturn(initialMeta)
        whenever(initialMeta.selected).thenReturn(true)

        whenever(dataA.id).thenReturn("Data A id")
        whenever(dataA.meta).thenReturn(metaA)
        whenever(dataB.id).thenReturn("Data B id")
        whenever(dataB.meta).thenReturn(metaB)
    }

    /**
     * Ничего не выбрано и первое назначение
     */
    @Test
    fun `When item assigned as selected with empty selection and it was the first action, then selection should be completed`() {
        mockInitialSelection()

        modeHandler.setSelected(dataA)

        verify(completeFunction, only()).invoke(dataA)
    }

    /**
     * Что-то выбрано до назначения (проверка выбора)
     */
    @Test
    fun `When item assigned as selected and it wasn't the first action, then item should be in selection list`() {
        mockInitialSelection()

        // какая-то манипуляция со списком выбранных от пользователя
        modeHandler.toggleSelection(dataB)
        modeHandler.setSelected(dataA)

        selection.awaitCount(2).assertValueAt(1, listOf(dataB, dataA))
    }

    /**
     * Что-то выбрано до назначения (проверка: не завершено)
     */
    @Test
    fun `When item assigned as selected and it was not the first action, then selection shouldn't be completed`() {
        mockInitialSelection()

        // какая-то манипуляция со списком выбранных от пользователя
        modeHandler.toggleSelection(dataB)
        modeHandler.setSelected(dataA)

        selection.awaitCount(2)
        verify(completeFunction, never()).invoke(any())
    }

    /**
     * Начальные данные и есть манипуляция до назначения
     */
    @Test
    fun `When item assigned as selected with empty selection and it was not the first action, then selection shouldn't be completed`() {
        mockInitialSelection(initialData)

        // действие, которое привело к опустошению списка
        modeHandler.toggleSelection(initialData)
        modeHandler.setSelected(dataA)

        selection.awaitCount(2)
        verify(completeFunction, never()).invoke(any())
    }

    /**
     * Начальные данные, но нет манипуляций до назначения
     */
    @Test
    fun `When item assigned as selected with selection and it was the first action, then selection shouldn't be completed`() {
        mockInitialSelection(initialData)

        modeHandler.setSelected(dataA)

        selection.awaitCount(2)
        verify(completeFunction, never()).invoke(any())
    }

    @Test
    fun `When selection overridden and next item selected, then both items should be selected`() {
        mockInitialSelection(initialData)

        modeHandler.setSelected(dataA)
        modeHandler.setSelected(dataB)

        selection.awaitCount(3).assertValues(listOf(initialData), listOf(dataA), listOf(dataA, dataB))
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=352eb719-d53a-4055-a95b-02054ba4f1e1
     */
    @Test
    fun `When item assigned as selected with selection and it was the first action, then selection should be overridden`() {
        mockInitialSelection(initialData)

        modeHandler.setSelected(dataA)

        selection.awaitCount(2).assertValues(listOf(initialData), listOf(dataA))
    }

    @Test
    fun `When item assigned as selected with empty selection and then next item was selected, then assignment should be delivered directly to result`() {
        mockInitialSelection()
        whenever(completeFunction.invoke(dataA)).then { completeSubject.onNext(Unit) }

        modeHandler.setSelected(dataA)
        modeHandler.setSelected(dataB)

        selection.assertNoValues()
        verify(completeFunction, only()).invoke(dataA)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=09aa8a83-96ff-4eb5-8cda-abfab4d808ba
     */
    @Test
    fun `When item assigned as selected with empty selection and it was the first action, then it should be delivered directly to result`() {
        mockInitialSelection()
        whenever(completeFunction.invoke(dataA)).then { completeSubject.onNext(Unit) }

        modeHandler.setSelected(dataA)

        selection.assertNoValues()
        verify(completeFunction, only()).invoke(dataA)
    }

    @Test
    fun `When selection limit exceeded by initial data and item assigned as selected, then it should override selection`() {
        mockInitialSelection(1, initialData)

        modeHandler.setSelected(dataA)

        limit.assertEmpty()
        selection.awaitCount(2).assertValues(listOf(initialData), listOf(dataA))
        verify(completeFunction, never()).invoke(any())
    }

    private fun mockInitialSelection(vararg initialSelection: TestData) =
        mockInitialSelection(DEFAULT_SELECTION_LIMIT, *initialSelection)

    private fun mockInitialSelection(selectionLimit: Int, vararg initialSelection: TestData) {
        val initialSelectionSingle = Single.just(initialSelection.toList())
        val commandHandler = SelectionCommandHandler<TestData>(Schedulers.single())
        val selectionObservable = commandHandler.startWith(initialSelectionSingle)
        modeHandler =
            OverrideSelectionModeHandler(commandHandler, selectionLimit, selectionObservable, completeFunction)

        selection = selectionObservable.filterSelectedItems(completeSubject).test()
        limit = modeHandler.limitObservable.test()
    }
}