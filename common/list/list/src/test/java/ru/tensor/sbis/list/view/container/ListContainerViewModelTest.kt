package ru.tensor.sbis.list.view.container

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.mockito.kotlin.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import ru.tensor.sbis.list.base.presentation.StubLiveData
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

class ListContainerViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: ListContainerViewModelImpl
    private val stubViewVisibility = MutableLiveData<Int>()
    private val progressVisibility = MutableLiveData<Int>()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ListContainerViewModelImpl(
            stubViewVisibility,
            progressVisibility,
            _stubContent = StubLiveData()
        )
        viewModel.progressVisibility.observeForever { }
        viewModel.stubViewVisibility.observeForever { }
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        testDispatcher.advanceUntilIdle()
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Initially show nothing`() {
        val viewModel = ListContainerViewModelImpl()
        viewModel.progressVisibility.observeForever { }
        viewModel.stubViewVisibility.observeForever { }
        viewModel.listVisibility.observeForever { }
        testDispatcher.advanceUntilIdle()

        assertEquals(INVISIBLE, viewModel.progressVisibility.value)
        assertEquals(INVISIBLE, viewModel.stubViewVisibility.value)
        assertEquals(INVISIBLE, viewModel.listVisibility.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun showStub() {
        hideStub()

        viewModel.showOnlyStub()

        verifyShowOnlyStub()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun showProgress() {
        hideProgress()

        viewModel.showOnlyProgress()

        verifyShowOnlyProgress()
    }

    @Test
    fun setStubContent() {
        val content = mock<StubViewContentFactory>()
        viewModel.setStubContentFactory(content)

        assertEquals(content, viewModel.stubContent.value)
    }

    @Test
    fun create() {
        ListContainerViewModelImpl()
    }

    private fun hideProgress() {
        progressVisibility.value = INVISIBLE
    }

    private fun hideStub() {
        stubViewVisibility.value = INVISIBLE
    }

    @ExperimentalCoroutinesApi
    private fun verifyShowOnlyStub() {
        runAfterIdle {
            assertEquals(VISIBLE, viewModel.stubViewVisibility.value)
            assertNotEquals(VISIBLE, viewModel.progressVisibility.value!!)
        }
    }

    @ExperimentalCoroutinesApi
    private fun verifyShowOnlyProgress() {
        runAfterIdle {
            assertEquals(VISIBLE, viewModel.progressVisibility.value)
            assertNotEquals(VISIBLE, viewModel.stubViewVisibility.value!!)
        }
    }

    @ExperimentalCoroutinesApi
    private fun runAfterIdle(check: () -> Unit) {
        testDispatcher.advanceUntilIdle()
        check()
    }
}