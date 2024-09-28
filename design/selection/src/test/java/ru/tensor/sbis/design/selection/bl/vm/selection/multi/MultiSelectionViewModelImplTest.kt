package ru.tensor.sbis.design.selection.bl.vm.selection.multi

import org.mockito.kotlin.*
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.DATA
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.AbstractSelectionModeHandler
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory

/**
 * Тест базовых механик [MultiSelectionViewModelImpl], которые актуальны для всех реализаций
 * [AbstractSelectionModeHandler] и всех режимов работы [SelectorSelectionMode]
 *
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class MultiSelectionViewModelImplTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val selectionTestLimit = 2
    private val dataId = " Test data id"
    private val limitDataId = " Test limit data id"

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: TestData

    @Mock
    private lateinit var limitMeta: SelectorItemMeta

    @Mock
    private lateinit var limitData: TestData

    @Mock
    private lateinit var selectionLoader: MultiSelectionLoader<TestData>

    @Mock
    private lateinit var metaFactory: ItemMetaFactory

    @Mock
    private lateinit var doneButtonViewModel: DoneButtonViewModel

    private lateinit var selection: TestObserver<List<TestData>>
    private lateinit var result: TestObserver<List<TestData>>
    private lateinit var limit: TestObserver<Int>

    @Before
    fun setUp() {
        // загрузка вызывается в фоновом потоке и в ряде сценариев может не успеть отработать
        lenient().`when`(selectionLoader.loadSelectedItems()).thenReturn(emptyList())
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When user select data, then it should be added to selection list`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)

        selection.awaitCount(1).assertValue(listOf(data))
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When user remove data from selection, then it should be removed from selection list`(
        mode: SelectorSelectionMode
    ) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(meta.selected).thenReturn(false, true)
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        val data2: DATA = mockData()

        vm.toggleSelection(data)
        vm.toggleSelection(data2)
        vm.toggleSelection(data)

        selection.awaitCount(3).assertValueAt(2, listOf(data2))
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    @Ignore("TODO: 10/14/2020 https://online.sbis.ru/opendoc.html?guid=9f61b969-0743-4ae9-aa2c-38261aea35d1")
    fun `When user completed selection, then selection result should be emitted`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)
        vm.complete()

        result.assertValue { it == listOf(data) }
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When user completed selection with empty list, then it should be emitted`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)

        vm.complete()

        result.await().assertValue(emptyList())
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When user cancel selection, then selection should complete`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)

        vm.cancel()

        result.assertNoValues().assertComplete()
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When user select data and cancel selection, then selection should complete`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)
        vm.cancel()

        result.assertNoValues().assertComplete()
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When item selected, then selection flag should be activated`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)

        selection.awaitCount(1)
        verify(meta).isSelected = true
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When item unselected, then selection flag should be cleared`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(meta.selected).thenReturn(false, true)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)

        selection.awaitCount(1)
        clearInvocations(meta)

        vm.toggleSelection(data)

        selection.awaitCount(2)
        verify(meta).isSelected = false
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When selection list changed, then completion rule should be invoked`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.meta).thenReturn(meta)

        vm.toggleSelection(data)

        selection.awaitCount(1)

        verify(doneButtonViewModel).setInitialData(emptyList())
        verify(doneButtonViewModel).setSelectedData(listOf(data))
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When selection limit exceed, then notification should be delivered`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        whenever(limitData.id).thenReturn(limitDataId)
        whenever(limitData.meta).thenReturn(limitMeta)
        val overhead = mockData()

        vm.toggleSelection(data)
        vm.toggleSelection(limitData)
        vm.toggleSelection(overhead)

        limit.awaitCount(1).assertValueCount(1)
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When selection limit exceed, then selection list should not be changed`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        whenever(limitData.id).thenReturn(limitDataId)
        whenever(limitData.meta).thenReturn(limitMeta)
        val overhead = mockData()

        vm.toggleSelection(data)
        vm.toggleSelection(limitData)
        vm.toggleSelection(overhead)

        selection.awaitCount(3).assertValues(listOf(data), listOf(data, limitData), listOf(data, limitData))
    }

    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When limit exceed and item replaced to another, then item should be replaced`(mode: SelectorSelectionMode) {
        val vm = mockViewModelForSelectionMode(mode)
        whenever(meta.selected).thenReturn(false, true)
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        whenever(limitData.id).thenReturn(limitDataId)
        whenever(limitData.meta).thenReturn(limitMeta)
        val overhead = mockData()

        vm.toggleSelection(data)
        vm.toggleSelection(limitData)
        // этим вызовом отошли от предела
        vm.toggleSelection(data)
        vm.toggleSelection(overhead)

        selection.awaitCount(4).assertValues(
            listOf(data),
            listOf(data, limitData),
            listOf(limitData),
            listOf(limitData, overhead)
        )
    }

    private fun mockViewModelForSelectionMode(mode: SelectorSelectionMode): MultiSelectionViewModelImpl<TestData> {
        // для тестирования ограничения достаточно 1, для остальных тестов нет
        val vm = MultiSelectionViewModelImpl(
            selectionLoader,
            metaFactory,
            selectionTestLimit,
            mode,
            doneButtonViewModel,
            Schedulers.single()
        )
        selection = vm.selection.test()
        result = vm.result.test()
        limit = vm.limitExceed.test()

        return vm
    }

    private fun mockData() = mock<TestData> {
        on { meta } doReturn mock()
        on { id } doReturn ""
    }
}
