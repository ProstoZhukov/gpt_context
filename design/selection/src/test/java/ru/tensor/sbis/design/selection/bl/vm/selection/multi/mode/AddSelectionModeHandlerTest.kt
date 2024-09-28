package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import org.mockito.kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.DEFAULT_SELECTION_LIMIT
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class AddSelectionModeHandlerTest {

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

    private lateinit var modeHandler: AddSelectionModeHandler<TestData>
    private lateinit var selection: TestObserver<List<TestData>>
    private lateinit var limit: TestObserver<Int>

    @Before
    fun setUp() {
        whenever(dataA.id).thenReturn("Data A id")
        whenever(dataA.meta).thenReturn(metaA)
        whenever(dataB.id).thenReturn("Data B id")
        whenever(dataB.meta).thenReturn(metaB)
    }

    /**
     * Назначение производится первым действием
     */
    @Test
    fun `When item assigned as selected and it was the first action, then item should be in selection list`() {
        mockInitialSelection()

        modeHandler.setSelected(dataA)

        selection.awaitCount(1).assertValue(listOf(dataA))
    }

    /**
     * Что-то выбрано до назначения (проверка выбора)
     */
    @Test
    fun `When item assigned as selected and it wasn't the first action, then item should be in selection list`() {
        mockInitialSelection()

        modeHandler.toggleSelection(dataB)
        modeHandler.setSelected(dataA)

        selection.awaitCount(2).assertValues(listOf(dataB), listOf(dataB, dataA))
    }

    @Test
    fun `When items consequently selected, then both items should be selected`() {
        mockInitialSelection()

        modeHandler.setSelected(dataA)
        modeHandler.setSelected(dataB)

        selection.awaitCount(2).assertValues(listOf(dataA), listOf(dataA, dataB))
    }

    @Test
    fun `When item assigned as selected with initial selection, then item should be inserted at the end`() {
        mockInitialSelection(0, initialData)

        modeHandler.setSelected(dataA)

        selection.awaitCount(2).assertValues(listOf(initialData), listOf(initialData, dataA))
    }

    @Test
    fun `When selection limit exceeded by initial data and item assigned as selected, item should not be inserted into selection list`() {
        val selectionLimit = 1
        mockInitialSelection(selectionLimit, 0L, initialData)

        modeHandler.setSelected(dataA)

        limit.awaitCount(1).assertValue(selectionLimit)
        selection.awaitCount(1)
            .assertValues(listOf(initialData), listOf(initialData))
            .assertNever { dataA in it }
    }

    @Test
    fun `When initial loading take time, then assigned item still should be inserted at the end of list`() {
        val delay = Random.nextLong(10, 500)
        mockInitialSelection(delay, initialData)

        modeHandler.setSelected(dataA)

        selection.withTag("Initial load delay is $delay ms")
            .awaitCount(2)
            .assertValues(listOf(initialData), listOf(initialData, dataA))
    }

    private fun mockInitialSelection(initialLoadDelay: Long = 0L, vararg initialSelection: TestData) =
        mockInitialSelection(DEFAULT_SELECTION_LIMIT, initialLoadDelay, *initialSelection)

    private fun mockInitialSelection(selectionLimit: Int, initialLoadDelay: Long, vararg initialSelection: TestData) {
        val initialSelectionSingle = Single
            .fromCallable { initialSelection.toList() }
            .delay(initialLoadDelay, TimeUnit.MILLISECONDS, Schedulers.single())
        val commandHandler = SelectionCommandHandler<TestData>(Schedulers.single())
        val selectionObservable = commandHandler.startWith(initialSelectionSingle)
        modeHandler = AddSelectionModeHandler(commandHandler, selectionLimit)

        selection = selectionObservable.skipWhile(List<*>::isEmpty).test()
        limit = modeHandler.limitObservable.test()
    }
}