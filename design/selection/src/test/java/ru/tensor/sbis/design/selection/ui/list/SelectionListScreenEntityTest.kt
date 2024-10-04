package ru.tensor.sbis.design.selection.ui.list

import android.content.Context
import io.mockk.mockk
import org.mockito.kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.Data
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubInfo
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode.PREFETCH
import ru.tensor.sbis.design.selection.ui.contract.list.ResultMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.filter.MultiSelectorListFilterMetaFactory
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.ListItem
import ru.tensor.sbis.design.selection.ui.utils.MultiRecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.utils.MultiSelectionFilterFunction
import ru.tensor.sbis.design.selection.ui.utils.MultiSelectionMergeFunction
import ru.tensor.sbis.design.selection.ui.utils.stub.StubContentProviderAdapter
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.ChooseAllFixedButtonViewModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import ru.tensor.sbis.list.view.utils.Plain

/**
 * Интеграционные тесты для связок из:
 * - [SelectionListScreenEntity]
 * - [ResultMapper]
 * - [PagingEntity]
 * - [SelectorFilterCreator]
 *
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionListScreenEntityTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var serviceResult: Any

    @Mock
    private lateinit var metaA: SelectorItemMeta

    @Mock
    private lateinit var metaB: SelectorItemMeta

    @Mock
    private lateinit var dataA: SelectorItemModel

    @Mock
    private lateinit var dataB: SelectorItemModel

    @Mock
    private lateinit var listItemA: ListItem

    @Mock
    private lateinit var listItemB: ListItem

    @Mock
    private lateinit var helper: ResultHelper<Any, Any>

    @Mock
    private lateinit var prefetchCheckFunction: PrefetchCheckFunction<SelectorItemModel>

    @Mock
    private lateinit var listMapper: ListMapper<Any, SelectorItemModel>

    @Mock
    private lateinit var listItemMapper: ListItemMapper

    @Mock
    private lateinit var metaFactory: ItemMetaFactory

    @Mock
    private lateinit var filterFactory: FilterFactory<SelectorItemModel, Any, Any>

    @Mock
    private lateinit var buttonVm: ChooseAllFixedButtonViewModel

    @Mock
    private lateinit var stubContentProvider: SelectorStubContentProvider<Any>

    @Mock
    private lateinit var workerThreadCheck: () -> Unit

    @Mock
    private lateinit var selectionVm: MultiSelectionViewModel<SelectorItemModel>

    private lateinit var pagingEntity: PagingEntity<Any, Any, Any>

    private lateinit var pagingDataValue: List<Any>

    private lateinit var filterCreator: SelectorFilterCreator<Any, Any>

    private lateinit var resultMapper: ResultMapper<Any>

    private lateinit var entity: SelectionListScreenEntity<Any, Any, Any>

    @Before
    fun setUp() {
        whenever(dataA.meta).thenReturn(metaA)
        whenever(dataB.meta).thenReturn(metaB)
        pagingDataValue = listOf(serviceResult)

        whenever(listItemA.data).thenReturn(dataA)
        whenever(listItemB.data).thenReturn(dataB)
        whenever(listItemMapper.toItem(dataA)).thenReturn(listItemA)
        whenever(listItemMapper.toItem(dataB)).thenReturn(listItemB)
        whenever(listMapper.invoke(serviceResult)).thenReturn(listOf(dataA, dataB))

        resultMapper = spy(
            ResultMapper(
                listMapper,
                listItemMapper,
                metaFactory,
                buttonVm,
                MultiSelectionFilterFunction(),
                MultiSelectionMergeFunction(selectionVm),
                MultiRecentSelectionCachingFunction()
            )
        )
        val stubContentProviderAdapter = StubContentProviderAdapter(stubContentProvider)
        pagingEntity = PagingEntity(resultMapper, helper, stubContentProviderAdapter)
        filterCreator = SelectorFilterCreator(filterFactory, MultiSelectorListFilterMetaFactory(), null)
        entity = SelectionListScreenEntity(
            resultMapper,
            filterCreator,
            prefetchCheckFunction,
            stubContentProviderAdapter,
            pagingEntity,
            workerThreadCheck
        )
    }

    @Test
    fun `When one data cycle completed, then mapper should be called once`() {
        entity.update(0, serviceResult)
        entity.isStub()
        entity.toListData()

        verify(resultMapper).map(pagingDataValue)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=be976b16-20af-428d-ba4f-5e6c8a4dac25
    @Test
    fun `When paging data contains only COMPLETE_SELECTION item, then entity should be stub`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        whenever(listMapper.invoke(serviceResult)).thenReturn(listOf(dataA))

        entity.update(0, serviceResult)

        assertTrue(entity.isStub())
    }

    @Test
    fun `When item list contains not only COMPLETE_SELECTION item, then stub should not be shown`() {
        entity.update(0, serviceResult)

        assertFalse(entity.isStub())
    }
    //endregion

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=964f011a-83bc-4a48-827f-8de9d7bd1e33
     */
    @Test
    fun `When paging data contains only COMPLETE_SELECTION item and has more, then entity should not be stub`() {
        whenever(metaA.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        whenever(listMapper.invoke(serviceResult)).thenReturn(listOf(dataA))
        whenever(helper.hasNext(serviceResult)).thenReturn(true)

        entity.update(0, serviceResult)

        assertFalse(entity.isStub())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=f35b844b-f693-403f-b1ab-2ed7f64648a4
     */
    @Test
    fun `When user choose all items, then data source should emit corresponding stub`() {
        // TODO: 9/23/2020 реализовать тест https://online.sbis.ru/opendoc.html?guid=8de2a362-6221-49c5-9f5c-7e6a73951ac5
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=18e08daa-d2da-4d8f-acfe-4e13128b7f21
     */
    @Test
    fun `When data source return empty list on query, then it should be emitted`() {
        // TODO: 9/23/2020 реализовать тест https://online.sbis.ru/opendoc.html?guid=8de2a362-6221-49c5-9f5c-7e6a73951ac5
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=b9dcc174-9528-4842-92b7-26b68c3f4fac
     */
    @Test
    fun `When service result is stub, then it should be delivered for stub content provider`() {
        val stubInfoCaptor = argumentCaptor<SelectorStubInfo<Any>>()
        whenever(stubContentProvider.provideStubViewContentFactory(any())).thenReturn { mockk() }

        entity.update(0, serviceResult)
        entity.provideStubViewContentFactory().invoke(context)

        verify(stubContentProvider, only()).provideStubViewContentFactory(stubInfoCaptor.capture())
        assertEquals(Data(serviceResult), stubInfoCaptor.allValues.single())
    }

    @Test
    fun `When all items are selected, then should return AllItemsSelectedStub`() {
        // TODO: 9/23/2020 реализовать тест https://online.sbis.ru/opendoc.html?guid=8de2a362-6221-49c5-9f5c-7e6a73951ac5
    }

    @Test
    fun `When not all items are selected, then should return result of MultiSelectionCombinerFunction`() {
        // TODO: 9/23/2020 реализовать тест https://online.sbis.ru/opendoc.html?guid=8de2a362-6221-49c5-9f5c-7e6a73951ac5
    }

    @Test
    fun `When prefetch required, then prefetch function should be called`() {
        val availableItems = listOf(dataA)
        whenever(listMapper.invoke(serviceResult)).thenReturn(availableItems)
        whenever(prefetchCheckFunction.needToPrefetch(any(), eq(availableItems))).thenReturn(PREFETCH)

        entity.update(0, serviceResult)

        assertSame(PREFETCH, entity.needToPrefetch())
        verify(prefetchCheckFunction, only()).needToPrefetch(any(), eq(availableItems))
    }

    @Test
    fun `When prefetch doesn't required, then prefetch function should be called`() {
        val availableItems = listOf(dataA, dataB)
        whenever(listMapper.invoke(serviceResult)).thenReturn(availableItems)
        whenever(prefetchCheckFunction.needToPrefetch(any(), eq(availableItems))).thenReturn(null)

        entity.update(0, serviceResult)

        assertNull(entity.needToPrefetch())
        verify(prefetchCheckFunction, only()).needToPrefetch(any(), eq(availableItems))
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=62088b3b-ee7e-45ee-af42-31bdcefe6d83
    @Test
    fun `When entity data is not loaded yet, then entity is not stub`() {
        assertFalse(entity.isStub())
        verifyNoMoreInteractions(helper)
    }

    @Test
    fun `When entity data updated at least one time and it still empty, then entity should be stub`() {
        whenever(helper.isStub(serviceResult)).thenReturn(true)

        entity.update(0, serviceResult)

        assertTrue(entity.isStub())
    }
    //endregion

    /**
     * Дешёвое решение для ситуаций, когда запускается обновление при сбросе строки поиска и изменении списка выбранных.
     * Т.к. нет возможности подписаться на потоки обновления [ListInteractor], нужен механизм их игнорирования
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=78b1df27-6d66-4cc9-8558-f99fbaea32cf
     */
    @Test
    fun `When data cleared, then entity is not stub until data loaded`() {
        whenever(helper.isStub(serviceResult)).thenReturn(true)

        entity.update(0, serviceResult)
        assertTrue(entity.isStub())

        entity.cleanPagesData()
        // пока не получили результат обновления, не отображаем заглушку. Она показывается только на основе данных
        assertFalse(entity.isStub())

        entity.update(0, serviceResult)
        assertTrue(entity.isStub())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=e5f29a9f-ea38-4e27-acf0-9273fc1d11a4
     */
    @Test
    fun `Given entity with selected item, when it updated by search request, then items should be filtered out`() {
        val searchQuery = "Test search query"
        whenever(dataA.title).thenReturn(searchQuery)
        whenever(metaA.queryRanges).thenReturn(mock())
        // после ввода поискового запроса контроллер вернул пустой результат
        whenever(listMapper.invoke(serviceResult)).thenReturn(emptyList())

        // выбор какого-то элемента перед поиском
        entity.setSelection(listOf(dataA))
        // перевод в режим поиска
        filterCreator.searchQuery = searchQuery
        // обновление при получении ответа от контроллера о результатах поиска
        entity.update(0, serviceResult)

        assertEquals(emptyList<SelectorItemModel>(), filterCreator.availableItems)
        assertEquals(Plain(emptyList()), entity.toListData())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=2e4132f4-9205-4cfd-8e79-bcce3b5ebfe3
     */
    @Test
    fun `Given entity with selected items, when selected items count is more than service result count, then available items should not contain selected items`() {
        val searchQuery = "Test search query"
        val extraData: SelectorItemModel = mock { on { meta } doReturn mock() }
        whenever(extraData.title).thenReturn("ExtraData test title")
        whenever(dataA.title).thenReturn("DataA test title")
        // отфильтрованный контроллером элемент
        whenever(dataB.title).thenReturn(searchQuery)
        // результат контролера тоже отфильтрованный, не должен содержать dataA
        whenever(listMapper.invoke(serviceResult)).thenReturn(listOf(dataB))

        entity.setSelection(listOf(dataA, extraData))
        filterCreator.searchQuery = searchQuery
        entity.update(0, serviceResult)

        assertEquals(listOf(dataB), filterCreator.availableItems)
        assertEquals(Plain(listOf(listItemB)), entity.toListData())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=6f82935c-54ef-4236-bb23-4b7fc806afc0
     */
    @Test
    fun `Given entity with selected item, when selected item match the search query and no service result loaded, then entity is not a stub`() {
        val searchQuery = "Test search query"
        whenever(dataA.title).thenReturn(searchQuery)
        val queryRanges: List<IntRange> = mock { on { isEmpty() } doReturn false }
        whenever(metaA.queryRanges).thenReturn(queryRanges)
        whenever(listMapper.invoke(serviceResult)).thenReturn(emptyList())
        // без этого мока логика уйдёт в ветку, которая не реагирует на наличие выбранных элементов
        Mockito.lenient().`when`(helper.isEmpty(serviceResult)).thenReturn(true)
        // было что-то выбрано
        entity.setSelection(listOf(dataA))
        // получили поисковую строку и закрепили список выбранных (стандартное поведение)
        filterCreator.searchQuery = searchQuery

        entity.update(0, serviceResult)

        assertFalse(entity.isStub())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=8995915e-9672-4111-8518-fffbc2d64338
     */
    @Test
    fun `Given entity with data, when visibility is expanded, then entity is not a stub`() {
        Mockito.lenient().`when`(helper.isStub(serviceResult)).thenReturn(true)

        entity.update(0, serviceResult)
        entity.setSelection(listOf(dataA))

        assertFalse(entity.isStub())
    }
}
