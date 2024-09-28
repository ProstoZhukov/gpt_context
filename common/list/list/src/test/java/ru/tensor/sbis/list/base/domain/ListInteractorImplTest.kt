package ru.tensor.sbis.list.base.domain

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.domain.fetcher.RepositoryFetcher

@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class ListInteractorImplTest {

    private val mockView = mock<View<TestEntity>>()
    private val filterForNextPage0 = mock<FilterAndPageProvider<String>>()
    private val filterForNextPage1 = mock<FilterAndPageProvider<String>>()
    private val filterForPrevious0 = mock<FilterAndPageProvider<String>>()
    private val filterForPrevious1 = mock<FilterAndPageProvider<String>>()
    private val mockEntity = mock<TestEntity>()
    private val mockFetcher = mock<RepositoryFetcher<TestEntity, String>>()
    private val interactor = ListInteractorImpl(mockFetcher)

    @Test
    fun nextPage() {
        val disposable = PublishSubject.create<String>().subscribe()
        whenever(mockEntity.provideFilterForNextPage()).doReturn(filterForNextPage0).thenReturn(filterForNextPage1)
        whenever(mockFetcher.updateListEntity(mockEntity, filterForNextPage0, mockView)).doReturn(disposable)

        assertEquals(disposable, interactor.nextPage(mockEntity, mockView))
    }

    @Test
    fun previousPage() {
        val disposable = PublishSubject.create<String>().subscribe()
        whenever(mockEntity.provideFilterForPreviousPage()).doReturn(filterForPrevious0).thenReturn(filterForPrevious1)
        whenever(mockFetcher.updateListEntity(mockEntity, filterForPrevious0, mockView)).doReturn(disposable)

        assertEquals(disposable, interactor.previousPage(mockEntity, mockView))
    }

    @Test
    fun refresh() {
        val disposable0 = PublishSubject.create<String>().subscribe()
        val disposable1 = PublishSubject.create<String>().subscribe()
        whenever(mockEntity.provideFilterForNextPage()).doReturn(filterForNextPage0)
        whenever(mockFetcher.updateListEntity(mockEntity, filterForNextPage0, mockView))
            .doReturn(disposable0)
            .thenReturn(disposable1)
        interactor.nextPage(mockEntity, mockView)

        assertEquals(disposable1, interactor.refresh(mockEntity, mockView))
    }

    @Test
    fun default() {
        ListInteractorImpl(mock<RepositoryFetcher<TestEntity, String>>())
    }

    interface TestEntity : ListScreenEntity,
        FilterProvider<String>
}