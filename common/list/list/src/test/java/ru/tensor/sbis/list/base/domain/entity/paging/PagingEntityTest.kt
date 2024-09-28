package ru.tensor.sbis.list.base.domain.entity.paging

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.Mapper
import ru.tensor.sbis.list.base.domain.entity.paging.filter.FilterFactory
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider
import ru.tensor.sbis.list.view.utils.Plain

@RunWith(JUnitParamsRunner::class)
internal class PagingEntityTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)
    private val result0 = mock<Result>()
    private val resultEmpty = mock<Result>()
    private val mockMapper = mock<Mapper<Result>>()
    private val mockHelper = mock<ResultHelper<Anchor, Result>> {
        on { isEmpty(result0) } doReturn false
        on { isEmpty(resultEmpty) } doReturn true
    }
    private val mockStubContentProvider = mock<StubContentProvider<Result>>()
    private val mockPagingData = mock<PagingData<Result>>()
    private val mockFilterFactory = mock<FilterFactory<Anchor, Result, Filter>>()
    private val pagingEntity = PagingEntity(
        mockMapper,
        mockHelper,
        mockStubContentProvider,
        itemsOnPage,
        mockPagingData,
        mockFilterFactory
    )
    private val page = 333

    @Test
    fun default() {
        PagingEntity<Anchor, Result, Filter>(
            mockMapper,
            mockHelper,
        )
    }

    @Test
    @Parameters("true", "false")
    fun hasNext(hasNext: Boolean) {
        whenever(mockPagingData.hasNext()).doReturn(hasNext)

        assertEquals(hasNext, pagingEntity.hasNext())
    }

    @Test
    @Parameters("true", "false")
    fun hasPrevious(hasPrevious: Boolean) {
        whenever(mockPagingData.hasPrevious()).doReturn(hasPrevious)

        assertEquals(hasPrevious, pagingEntity.hasPrevious())
    }

    @Test
    fun cleanPagesData() {
        pagingEntity.cleanPagesData()

        verify(mockPagingData).clear()
    }

    @Test
    fun isStub() {
        whenever(mockPagingData.isStub()).doReturn(true).thenReturn(false)

        assertTrue(pagingEntity.isStub())
        assertFalse(pagingEntity.isStub())
    }

    @Test
    fun provideStubContent() {
        val defaultStubContent = mock<StubViewContentFactory>()
        whenever(mockStubContentProvider.provideStubViewContentFactory(null)) doReturn defaultStubContent

        assertEquals(defaultStubContent, pagingEntity.provideStubViewContentFactory())
    }

    @Test
    fun toListData() {
        val listOfValues = mock<List<Result>>()
        whenever(mockPagingData.listOfValues()) doReturn listOfValues
        val mappedData = Plain(listOf(mock()))
        whenever(mockMapper.map(listOfValues)) doReturn mappedData

        assertEquals(mappedData, pagingEntity.toListData())
    }

    @Test
    fun filterForNext() {
        val createFilter = mock<CreateFilter<Anchor, Filter>>()
        val filter = mock<FilterAndPageProvider<Filter>>()
        whenever(mockFilterFactory.filterForNext(createFilter)) doReturn filter

        assertEquals(filter, pagingEntity.filterForNext(createFilter))
    }

    @Test
    fun filterForPrevious() {
        val createFilter = mock<CreateFilter<Anchor, Filter>>()
        val filter = mock<FilterAndPageProvider<Filter>>()
        whenever(mockFilterFactory.filterForPrevious(createFilter)) doReturn filter

        assertEquals(filter, pagingEntity.filterForPrevious(createFilter))
    }

    @Test
    fun update() {
        pagingEntity.update(page, result0)

        verify(mockPagingData).update(page, result0)
    }

    /**
     * Первичная загрузка дынных, примимаем первый элемент, даже если он пустой,
     * чтобы извлечь в дальнейшем флаг hasNext, который используем для показа индикатора загрузки.
     */
    @Test
    fun `When is empty and update with empty then should add empty`() {
        whenever(mockPagingData.isEmpty()).doReturn(true)
        pagingEntity.update(page, resultEmpty)

        verify(mockPagingData).update(page, resultEmpty)
    }

//    /**todo
//     * Если уже были данные и следующая порция определяется как пустый данны, это может значить только то, что пропала
//     * сеть и не смогли подгрузить новую порцию данных, когда в предыдущей был флаг hasNext = true.
//     */
//    @Test
//    fun `When is not empty and update with empty then should add empty`() {
//        whenever(mockPagingData.isEmpty()).doReturn(true)
//        whenever(mockPagingData.isEmpty()).doReturn(false)
//
//        pagingEntity.update(page, result0)
//        pagingEntity.update(page, resultEmpty)
//
//        verify(mockPagingData).update(page, result0)
//        verify(mockPagingData, never()).update(page, resultEmpty)
//    }
}

private const val itemsOnPage: Long = 15

private class Anchor
private class Result
private class Filter