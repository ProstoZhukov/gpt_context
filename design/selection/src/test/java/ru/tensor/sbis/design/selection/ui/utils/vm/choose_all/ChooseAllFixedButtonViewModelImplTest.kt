package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.mockito.kotlin.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class ChooseAllFixedButtonViewModelImplTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: SelectorItemModel

    private val viewModel = ChooseAllFixedButtonViewModelImpl()

    @Before
    fun setUp() {
        viewModel.showFixedButton.observeForever { }
    }

    @Test
    fun `When observer attach to visibility live data, then observer receive GONE visibility by default`() {
        val observer: Observer<Int> = mock()

        viewModel.showFixedButton.observeForever(observer)

        verify(observer, only()).onChanged(GONE)
    }

    @Test
    fun `When data is set with visible stub view and choose all button clicked, then data should be delivered to subscribers`() {
        val observer = viewModel.fixedButtonClicked.test()
        mockData()
        viewModel.setData(data)
        viewModel.setStubVisible(VISIBLE)

        viewModel.onFixedButtonClicked()

        observer.assertValue(data)
    }

    @Test
    fun `When choose all button clicked on data list, then exception should be thrown`() {
        val observer = viewModel.fixedButtonClicked.test()
        mockData()
        viewModel.setData(data)
        // заглушка когда-то была показана, но затем скрыта
        viewModel.setStubVisible(VISIBLE)
        viewModel.setStubVisible(GONE)

        viewModel.onFixedButtonClicked()

        observer.assertError(IllegalStateException::class.java)
    }

    @Test
    fun `When choose all button clicked without data, then nothing should be delivered`() {
        val observer = viewModel.fixedButtonClicked.test()
        viewModel.setStubVisible(VISIBLE)

        viewModel.onFixedButtonClicked()

        observer.assertEmpty()
    }

    @Test
    fun `When data is set with visible stub, then button should be visible`() {
        mockData()

        viewModel.setStubVisible(VISIBLE)
        viewModel.setData(data)

        assertEquals(VISIBLE, viewModel.showFixedButton.value)
    }

    @Test
    @Parameters(method = "visibilities")
    fun `When data is absent, then stub visibility shouldn't change button visibility`(stubVisibility: Int) {
        viewModel.setStubVisible(stubVisibility)

        assertEquals(GONE, viewModel.showFixedButton.value)
    }

    @Test
    @Parameters(method = "visibilities")
    fun `When data is present, then button visibility depends on stub visibility`(stubVisibility: Int) {
        // в тесте проверяем только финальное значение. Чтобы избежать фильтрации, устанавливаем противоположное
        val previousVisibility = GONE/*stubVisibility.not()*/
        mockData()
        viewModel.setData(data)
        viewModel.setStubVisible(previousVisibility)

        viewModel.setStubVisible(stubVisibility)

        assertEquals(stubVisibility, viewModel.showFixedButton.value)
    }

    @Test
    fun `When data is present, then button visibility shouldn't change for invisible stub`() {
        mockData()
        viewModel.setData(data)

        viewModel.setStubVisible(GONE)

        assertEquals(GONE, viewModel.showFixedButton.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When data handle strategy is not equal to COMPLETE_SELECTION, then exception should be thrown`() {
        mockData()
        // выберем произвольное значение, кроме легального
        val handleStrategy = ClickHandleStrategy
            .values()
            .filter { it != ClickHandleStrategy.COMPLETE_SELECTION }
            .random()
        whenever(meta.handleStrategy).thenReturn(handleStrategy)

        viewModel.setData(data)
    }

    @Suppress("unused")
    private fun visibilities() = listOf(VISIBLE, GONE)

    private fun mockData() {
        whenever(meta.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        whenever(data.meta).thenReturn(meta)
    }
}