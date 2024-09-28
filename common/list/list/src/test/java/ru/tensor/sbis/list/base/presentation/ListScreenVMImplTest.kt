package ru.tensor.sbis.list.base.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListScreenVMImpTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()
    private val mockInteractor = mock<ListInteractor<ListScreenEntity>>()
    private val mockPlainListVM = mock<PlainListVM<ListScreenEntity>>()
    private val mockEntity = mock<ListScreenEntity>()
    private val listScreenVMImpl = ListScreenVMImpl(
        mockInteractor,
        mockEntity,
        mockPlainListVM
    )

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearInvocations(mockPlainListVM, mockInteractor, mockEntity)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Call next page on start`() {
        ListScreenVMImpl(mockInteractor, mockEntity, mockPlainListVM)

        verify(mockInteractor).firstPage(mockEntity, mockPlainListVM)
    }

    @Test
    fun loadPrevious() {
        whenever(mockEntity.hasPrevious()).doReturn(true)
        listScreenVMImpl.loadPrevious()

        verify(mockInteractor).previousPage(mockEntity, mockPlainListVM)
    }

    @Test
    fun loadNext() {
        whenever(mockEntity.hasNext()).doReturn(true)
        listScreenVMImpl.loadNext()

        verify(mockInteractor).nextPage(mockEntity, mockPlainListVM)
    }

    @Test
    fun onRefresh() {
        listScreenVMImpl.showRefresh()

        verify(mockInteractor).refresh(mockEntity, mockPlainListVM)
    }

    @Test
    fun getFabPadding() {
        val listLiveData = mock<BooleanLiveData>()
        whenever(mockPlainListVM.fabPadding).doReturn(listLiveData)

        assertSame(listLiveData, listScreenVMImpl.fabPadding)
    }

    @Test
    fun getListLiveData() {
        val listLiveData = mock<ListLiveData>()
        whenever(mockPlainListVM.listData).doReturn(listLiveData)

        assertSame(listLiveData, listScreenVMImpl.listData)
    }

    @Test
    fun getListVisibility() {
        val listLiveData = mock<LiveData<Int>>()
        whenever(mockPlainListVM.listVisibility).doReturn(listLiveData)

        assertSame(listLiveData, listScreenVMImpl.listVisibility)
    }

    @Test
    fun getLoadNextVisibility() {
        val liveData = mock<BooleanLiveData>()
        whenever(mockPlainListVM.loadNextVisibility).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.loadNextVisibility)
    }

    @Test
    fun getLoadPreviousVisibility() {
        val liveData = mock<BooleanLiveData>()
        whenever(mockPlainListVM.loadPreviousVisibility).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.loadPreviousVisibility)
    }

    @Test
    fun getProgressVisibility() {
        val liveData = mock<LiveData<Int>>()
        whenever(mockPlainListVM.progressVisibility).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.progressVisibility)
    }

    @Test
    fun getStubContent() {
        val data = StubLiveData()
        whenever(mockPlainListVM.stubContent).doReturn(data)

        assertSame(data, listScreenVMImpl.stubContent)
    }

    @Test
    fun getStubViewVisibility() {
        val liveData = MutableLiveData<Int>()
        whenever(mockPlainListVM.stubViewVisibility).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.stubViewVisibility)
    }

    @Test
    fun getSwipeRefreshIsEnabled() {
        val liveData = mock<BooleanLiveData>()
        whenever(mockPlainListVM.swipeRefreshIsEnabled).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.swipeRefreshIsEnabled)
    }

    @Test
    fun getSwipeRefreshIsVisible() {
        val liveData = mock<BooleanLiveData>()
        whenever(mockPlainListVM.swipeRefreshIsVisible).doReturn(liveData)

        assertSame(liveData, listScreenVMImpl.swipeRefreshIsVisible)
    }

    @Test
    fun move() {
        listScreenVMImpl.move(2, 4)

        verify(mockPlainListVM).move(2, 4)
    }

    @Test
    fun setStubContent() {
        val content = mock<StubViewContentFactory>()
        listScreenVMImpl.setStubContentFactory(content)

        verify(mockPlainListVM).setStubContentFactory(content)
    }

    @Test
    fun showOnlyList() {
        listScreenVMImpl.showOnlyList()

        verify(mockPlainListVM).showOnlyList()
    }

    @Test
    fun showOnlyProgress() {
        listScreenVMImpl.showOnlyProgress()

        verify(mockPlainListVM).showOnlyProgress()
    }

    @Test
    fun showOnlyStub() {
        listScreenVMImpl.showOnlyStub()

        verify(mockPlainListVM).showOnlyStub()
    }

    @Test
    fun showData() {
        val entity1 = mock<ListScreenEntity>()
        listScreenVMImpl.showData(entity1)

        verify(mockPlainListVM).showData(entity1)
    }

    @Test
    fun showLoading() {
        listScreenVMImpl.showLoading()

        verify(mockPlainListVM).showLoading()
    }

    @Test
    fun showStub() {
        val stubEntity = mock<StubEntity>()
        listScreenVMImpl.showStub(stubEntity)

        verify(mockPlainListVM).showStub(stubEntity)
    }

    @Test
    fun default() {
        @Suppress("RemoveExplicitTypeArguments")
        ListScreenVMImpl<ListScreenEntity>(mock(), mock())
    }
}