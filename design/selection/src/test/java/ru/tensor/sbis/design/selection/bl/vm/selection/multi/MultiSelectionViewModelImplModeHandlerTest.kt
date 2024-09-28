package ru.tensor.sbis.design.selection.bl.vm.selection.multi

import org.mockito.kotlin.*
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.DEFAULT_SELECTION_LIMIT
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.AbstractSelectionModeHandler
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.DefaultSelectionModeHandlerFactory
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.SelectionModeHandlerFactory
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode.REPLACE_ALL
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import java.util.*
import java.util.concurrent.Executors
import kotlin.random.Random

/**
 * Тестирование взаимодействия [MultiSelectionViewModelImpl] и реализаций [AbstractSelectionModeHandler]
 * (включая интеграционные тесты)
 *
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class MultiSelectionViewModelImplModeHandlerTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val limitSubject = PublishSubject.create<Int>()

    private val uiThreadName = "Test ui thread"
    private val uiScheduler = Schedulers.from(Executors.newSingleThreadExecutor { Thread(it, uiThreadName) })

    @Mock
    private lateinit var initialDataA: TestData

    @Mock
    private lateinit var initialDataB: TestData

    @Mock
    private lateinit var metaA: SelectorItemMeta

    @Mock
    private lateinit var dataA: TestData

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

    private lateinit var vm: MultiSelectionViewModelImpl<TestData>
    private lateinit var selection: TestObserver<List<TestData>>
    private lateinit var result: TestObserver<List<TestData>>
    private lateinit var limit: TestObserver<Int>

    @After
    fun tearDown() {
        uiScheduler.shutdown()
    }

    @Test
    fun `When view model created, then mode handler factory should be invoked`() {
        mockInitialSelection(mockModeHandlerFactory())

        verify(modeHandlerFactory, only()).createSelectionHandler(eq(vm), any(), any(), eq(DEFAULT_SELECTION_LIMIT))
    }

    @Test
    fun `When mode handler limit emmit a value, then view model should emmit it`() {
        mockInitialSelection(mockModeHandlerFactory())
        val limitValue = Random.nextInt()

        limitSubject.onNext(limitValue)

        limit.awaitCount(1).assertValue(limitValue)
    }

    @Test
    fun `When toggleSelection method called on view model, then mode handler's method should be called`() {
        mockInitialSelection(mockModeHandlerFactory())

        vm.toggleSelection(dataA)

        verify(modeHandler).toggleSelection(dataA)
    }

    @Test
    fun `Given ClickHandleStrategy DEFAULT, when setSelected method called on view model, then mode handler's method should be called`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.DEFAULT)
        whenever(dataA.meta).thenReturn(metaA)
        whenever(modeHandler.setSelected(dataA)).thenReturn(mock())
        mockInitialSelection(mockModeHandlerFactory())

        vm.setSelected(dataA)

        verify(modeHandler).setSelected(dataA)
    }

    @Test
    fun `Given ClickHandleStrategy IGNORE, when setSelected method called on view model, then mode handler's method should not be called`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.IGNORE)
        whenever(dataA.meta).thenReturn(metaA)
        mockInitialSelection(mockModeHandlerFactory())

        vm.setSelected(dataA)

        verify(modeHandler, never()).setSelected(dataA)
    }

    @Test
    fun `Given ClickHandleStrategy COMPLETE_SELECTION, when setSelected method called on view model, then mode handler's method should not be called`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.IGNORE)
        whenever(dataA.meta).thenReturn(metaA)
        mockInitialSelection(mockModeHandlerFactory())

        vm.setSelected(dataA)

        verify(modeHandler, never()).setSelected(dataA)
    }

    @Test
    fun `When setSelected method called on COMPLETE_SELECTION item, then mode handler's method should not be called`() {
        whenever(dataA.meta).thenReturn(metaA)
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        mockInitialSelection(mockModeHandlerFactory())

        vm.setSelected(dataA)

        verify(modeHandler, never()).setSelected(any())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=bc13b3ee-ac17-4c44-a4d0-2d6b32958765
     */
    @Test
    fun `When item assigned as selected with empty selection and it was the first action, then complete rule should be invoked only with initial data`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.DEFAULT)
        whenever(dataA.meta).thenReturn(metaA)
        mockInitialSelection(DefaultSelectionModeHandlerFactory(SelectorSelectionMode.REPLACE_ALL_IF_FIRST))

        vm.setSelected(dataA)

        result.await().assertValue(listOf(dataA))
        verify(doneButtonViewModel).setInitialData(emptyList())
        // доставляться может только список начального выбора. Если выбор применён раньше, игнорируем начальные данные
        verify(doneButtonViewModel, never()).setSelectedData(argWhere { it != emptyList<TestData>() })
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=f0c2b4ae-b1b1-41aa-955d-1e7b430d0745
     */
    @Test
    fun `When item assigned as selected and it has COMPLETE_SELECTION handle strategy, then it should be delivered directly to result`() {
        val dataB: TestData = mock { on { meta } doReturn mock() }
        whenever(dataA.meta).thenReturn(metaA)
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        mockInitialSelection(DefaultSelectionModeHandlerFactory(SelectorSelectionMode.REPLACE_ALL_IF_FIRST))

        // добавим что-то в список выбранных
        vm.toggleSelection(dataB)
        // установим элемент, который переопределяет и завершает выбор
        vm.setSelected(dataA)

        result.await().assertValue(listOf(dataA))
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=8c26f2c0-bf16-4215-a82e-5f8717d319ce
     */
    @Test
    @Parameters(source = MultiSelectionViewModelImplModeHandlerTest::class)
    fun `When first item selected, then it should be inserted at the end of initial data`(mode: SelectorSelectionMode) {
        whenever(dataA.id).thenReturn("Test data A id")
        whenever(dataA.meta).thenReturn(metaA)
        mockInitialSelection(DefaultSelectionModeHandlerFactory(mode), initialDataA, initialDataB)

        vm.toggleSelection(dataA)

        selection.awaitCount(2).assertValueAt(1, listOf(initialDataA, initialDataB, dataA))
    }

    private fun mockInitialSelection(
        handlerFactory: SelectionModeHandlerFactory<TestData>,
        vararg initialSelection: TestData
    ) {
        // загрузка вызывается в фоновом потоке и в ряде сценариев может не успеть отработать
        lenient().`when`(selectionLoader.loadSelectedItems()).thenReturn(initialSelection.toList())
        vm = MultiSelectionViewModelImpl(
            selectionLoader,
            metaFactory,
            DEFAULT_SELECTION_LIMIT,
            handlerFactory,
            doneButtonViewModel,
            uiScheduler
        )
        selection = vm.selection.test()
        result = vm.result.test()
        limit = vm.limitExceed.test()
    }

    private fun mockModeHandlerFactory(): SelectionModeHandlerFactory<TestData> {
        whenever(modeHandler.limitObservable).thenReturn(limitSubject)
        whenever(
            modeHandlerFactory.createSelectionHandler(
                any(),
                any(),
                any(),
                eq(DEFAULT_SELECTION_LIMIT)
            )
        ).thenReturn(modeHandler)
        return modeHandlerFactory
    }

    companion object {

        @JvmStatic
        @Deprecated("TODO: 10/15/2020 https://online.sbis.ru/opendoc.html?guid=38c8da1e-30f8-4b81-aa7e-73b11a1fda21")
        internal fun provideSupportedModeList(): Array<SelectorSelectionMode> {
            val unsupported = arrayOf(REPLACE_ALL)
            return SelectorSelectionMode.values().asList().minus(unsupported).toTypedArray()
        }
    }
}