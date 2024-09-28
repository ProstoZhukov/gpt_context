package ru.tensor.sbis.list.base.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mockk.mockk
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.view.container.ListContainerViewModel
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain

@RunWith(JUnitParamsRunner::class)
class PlainListVMTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val entity = mock<ListScreenEntity>()
    private val mockListContainerViewModel = mock<ListContainerViewModel>()
    private val extractor = mock<BackgroundListDataExtractor>()
    private val listVM by lazy {
        PlainListVM<ListScreenEntity>(listContainerViewModel = mockListContainerViewModel, extractor = extractor)
    }

    @Before
    fun setUp() {
        listVM.loadNextVisibility.observeForever { }
        listVM.loadPreviousVisibility.observeForever { }
        listVM.loadNextAvailability.observeForever { }
        listVM.loadPreviousAvailability.observeForever { }
    }


    @Test
    fun getLoadNextVisibility() {
        assertFalse(listVM.loadNextVisibility.value!!)
    }

    @Test
    fun getLoadPreviousVisibility() {
        assertFalse(listVM.loadPreviousVisibility.value!!)
    }

    @Test
    fun getListLiveData() {
        assertTrue((listVM.listData.value as Plain).data.isEmpty())
    }

    @Test
    fun getSwipeRefreshIsVisible() {
        assertFalse(listVM.swipeRefreshIsVisible.value!!)
    }

    @Test
    fun getSwipeRefreshIsEnabled() {
        assertTrue(listVM.swipeRefreshIsEnabled.value!!)
    }

    @Test
    fun getFabPadding() {
        assertFalse(listVM.fabPadding.value!!)
    }

    @Test
    @Parameters("false, false", "false, true", "true, true", "true, false")
    fun showData(hasNext: Boolean, hasPrevious: Boolean) {
        val listData = mockk<ListData>()
        whenever(entity.toListData()) doReturn listData
        whenever(entity.hasNext()) doReturn hasNext
        whenever(entity.hasPrevious()) doReturn hasPrevious

        listVM.showData(entity)

        assertFalse(listVM.loadNextVisibility.value!!)
        assertFalse(listVM.loadPreviousVisibility.value!!)
        assertFalse(listVM.swipeRefreshIsVisible.value!!)

        verify(extractor).extract(entity, listVM)
    }

    @Test
    fun `When data show requested, then it should be set before list become visible`() {
        val dataObserver = mock<Observer<ListData>>()
        val orderVerification = inOrder(dataObserver, mockListContainerViewModel)
        val listData = mockk<ListData>()
        whenever(entity.toListData()).thenReturn(listData)
        listVM.listData.observeForever(dataObserver)

        listVM.receive(listData)

        orderVerification.verify(dataObserver).onChanged(listData)
        orderVerification.verify(mockListContainerViewModel).showOnlyList()
    }

    @Test
    fun showStub() {
        val entity = mock<StubEntity>()
        val stubContent = mock<StubViewContentFactory>()
        whenever(entity.provideStubViewContentFactory()) doReturn stubContent

        listVM.showStub(entity)

        assertFalse(listVM.loadNextVisibility.value!!)
        assertFalse(listVM.loadPreviousVisibility.value!!)
        verify(mockListContainerViewModel).setStubContentFactory(stubContent)
        verify(mockListContainerViewModel).showOnlyStub()
        assertFalse(listVM.swipeRefreshIsVisible.value!!)
        assertTrue(listVM.swipeRefreshIsEnabled.value!!)
    }


    @Test
    fun `When show stub or progress, then list data should be cleared`() {
        val progressIsVisible = PublishSubject.create<Boolean>()
        PlainListVM<ListScreenEntity>(
            progressIsVisible,
            listContainerViewModel = mockListContainerViewModel,
            extractor = extractor
        )
        val dataObserver = mock<Observer<ListData>>()
        whenever(entity.provideStubViewContentFactory()).thenReturn(mock())
        listVM.listData.observeForever(dataObserver)

        progressIsVisible.onNext(true)

        assertEquals(listVM.listData.value, Plain())
    }

    @Test
    fun showLoading() {
        listVM.showLoading()

        verify(mockListContainerViewModel).showOnlyProgress()
    }

    @Test
    fun loadPrevious() {
        listVM.loadPrevious()
    }

    @Test
    fun loadNext() {
        listVM.loadNext()
    }

    @Test
    fun onRefresh() {
        listVM.showRefresh()
    }

    @Test
    fun getListVisibility() {
        val liveData = MutableLiveData<Int>()
        whenever(mockListContainerViewModel.listVisibility).doReturn(liveData)

        assertSame(liveData, listVM.listVisibility)
    }

    @Test
    fun getProgressVisibility() {
        val liveData = MutableLiveData<Int>()
        whenever(mockListContainerViewModel.progressVisibility).doReturn(liveData)

        assertSame(liveData, listVM.progressVisibility)
    }

    @Test
    fun getStubViewVisibility() {
        val liveData = MutableLiveData<Int>()
        whenever(mockListContainerViewModel.stubViewVisibility).doReturn(liveData)

        assertSame(liveData, listVM.stubViewVisibility)
    }

    @Test
    fun showOnlyList() {
        listVM.showOnlyList()

        verify(mockListContainerViewModel).showOnlyList()
    }

    @Test
    fun showOnlyProgress() {
        listVM.showOnlyProgress()

        verify(mockListContainerViewModel).showOnlyProgress()
    }

    @Test
    fun showOnlyStub() {
        listVM.showOnlyStub()

        verify(mockListContainerViewModel).showOnlyStub()
    }
}