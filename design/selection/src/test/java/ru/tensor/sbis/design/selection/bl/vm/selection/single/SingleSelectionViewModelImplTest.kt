package ru.tensor.sbis.design.selection.bl.vm.selection.single

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import java.util.concurrent.TimeUnit

/**
 * Тест вьюмодели одиночного выбора
 *
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class SingleSelectionViewModelImplTest {

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: TestData
    @Mock
    private lateinit var selectionLoader: SingleSelectionLoader<TestData>

    private lateinit var vm: SingleSelectionViewModelImpl<TestData>

    private val testScheduler = TestScheduler()

    @Test
    fun `When initial selection received and there is no user selection yet, then initial item should be emitted`() {
        val selection = initializeAndGetSelectionObserver(data)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        selection.awaitCount(1).assertValue(data)
    }

    @Test
    fun `When initial selection received but there is a selection from user, then initial item should not be emitted and the result should contain selection from user`() {
        val selection = initializeAndGetSelectionObserver(data, initialSelectionDelayMs = 500)
        val result = vm.result.test()
        val selectedByUserData = mock<TestData>()
        whenever(selectedByUserData.meta).thenReturn(meta)

        vm.complete(selectedByUserData)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        selection.awaitCount(1).assertValue(selectedByUserData)
        result.assertValue(selectedByUserData)
    }

    @Test
    fun `When user completes selection, given item should be selected and emitted`() {
        val selection = initializeAndGetSelectionObserver()
        val result = vm.result.test()

        vm.complete(data)

        selection.awaitCount(1).assertValue(data)
        result.assertValue(data)
        verify(meta).isSelected = true
    }

    @Test
    fun `When user cancels selection, selection should be completed with no result`() {
        initializeAndGetSelectionObserver()
        val result = vm.result.test()

        vm.cancel()

        result.assertNoValues().assertComplete()
    }

    @Test
    fun `Given vm with no item selected, when selection updated, then result should be emitted`() {
        val selection = initializeAndGetSelectionObserver()
        val result = vm.result.test()

        vm.updateSelection(data)

        selection.assertEmpty()
        result.assertEmpty()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=9c55799f-641b-4ee8-a47e-08ebec48acdf
     */
    @Test
    fun `Given vm with item selected, when selection updated, then result should be emitted`() {
        val dataId = "Test data id"
        val initialSelection: TestData = mock {
            on { id } doReturn dataId
            on { meta } doReturn mock()
        }
        whenever(data.id).thenReturn(dataId)
        val selection = initializeAndGetSelectionObserver(initialSelection)
        val result = vm.result.test()

        testScheduler.triggerActions()
        vm.selection.firstOrError().subscribe { _, _ ->
            // имитируем состояние, когда обновление произошло после загрузки выбранного элемента
            vm.updateSelection(data)
        }

        selection.awaitCount(1).assertOf { it.values().last() === data }
        result.assertEmpty()
    }

    @Test
    fun `Given vm with item selected, when same item requested to update, then update should be ignored`() {
        val selection = initializeAndGetSelectionObserver(data)

        testScheduler.triggerActions()
        vm.selection.firstOrError().subscribe { _, _ ->
            // это обновление должно быт проигнорировано т.к. элемент уже обновлён
            vm.updateSelection(data)
        }

        selection.awaitCount(2).assertValue(data)
    }

    private fun initializeAndGetSelectionObserver(
        initialSelection: TestData? = null,
        initialSelectionDelayMs: Long = 0
    ): TestObserver<TestData> {
        whenever(data.meta).thenReturn(meta)
        whenever(selectionLoader.loadSelectedItem()).thenAnswer {
            Thread.sleep(initialSelectionDelayMs)
            initialSelection
        }

        vm = SingleSelectionViewModelImpl(selectionLoader, mock(), Schedulers.single(), testScheduler)
        return vm.selection.test()
    }

}