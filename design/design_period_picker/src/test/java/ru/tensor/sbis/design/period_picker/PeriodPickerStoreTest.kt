package ru.tensor.sbis.design.period_picker

import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.rx.observer
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStoreFactory
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerRouter
import java.util.GregorianCalendar

/**
 * Тестирование [PeriodPickerStore].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class PeriodPickerStoreTest {

    private val testMainDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private val startSelectionDate = GregorianCalendar(2023, 8, 10)
    private val endSelectionDate = GregorianCalendar(2023, 8, 19)

    private val dateFrom = GregorianCalendar(2023, 8, 1)
    private val dateTo = GregorianCalendar(2023, 8, 30)

    private lateinit var store: PeriodPickerStore
    private val router: PeriodPickerRouter = mock()

    @Before
    fun setup() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(testMainDispatcher)

        store = PeriodPickerStoreFactory(
            DefaultStoreFactory(),
            startSelectionDate,
            endSelectionDate,
            null,
            null,
            SbisPeriodPickerMode.YEAR
        ).create(StateKeeperDispatcher())
    }

    @After
    fun teardown() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
    }

    @Test
    fun `When mode is switched on MONTH then router opens month mode fragment`() = runTest {
        val states = ArrayList<PeriodPickerStore.State>()
        store.states(observer { states += it })
        store.labels(observer { it.handle(router) })

        val newMode = SbisPeriodPickerMode.MONTH
        store.accept(Intent.SwitchMode(newMode, null, null))

        verify(router).openMonthMode(null, null)
        assertEquals(newMode, states.last().mode)
    }

    @Test
    fun `When mode is switched on YEAR then router opens year mode fragment`() = runTest {
        val states = ArrayList<PeriodPickerStore.State>()
        store.states(observer { states += it })
        store.labels(observer { it.handle(router) })

        val newMode = SbisPeriodPickerMode.YEAR
        store.accept(Intent.SwitchMode(newMode, null, null))

        verify(router).openYearMode(null, null)
        assertEquals(newMode, states.last().mode)
    }

    @Test
    fun `When selection resets then router resets it`() = runTest {
        val states = ArrayList<PeriodPickerStore.State>()
        store.states(observer { states += it })
        store.labels(observer { it.handle(router) })

        store.accept(Intent.ResetSelection)

        verify(router).resetSelection()
        assertNull(states.last().startInitialPeriod)
        assertNull(states.last().endInitialPeriod)
        assertNull(states.last().startPeriod)
        assertNull(states.last().endPeriod)
    }

    @Test
    fun `When selection is updated then startPeriod and endPeriod are updated`() = runTest {
        val states = ArrayList<PeriodPickerStore.State>()
        store.states(observer { states += it })
        store.labels(observer { it.handle(router) })

        val tag = SbisPeriodPickerMode.MONTH.tag
        store.accept(
            Intent.UpdateSelection(
                tag = tag,
                dateFrom,
                dateTo
            )
        )

        verify(router).updateSelection(tag)
        assertEquals(dateFrom.timeInMillis, states.last().startPeriod?.timeInMillis ?: -1)
        assertEquals(dateTo.timeInMillis, states.last().endPeriod?.timeInMillis ?: -1)
    }

    @Test
    fun `When calendar is closed then startPeriod and endPeriod are updated`() = runTest {
        val states = ArrayList<PeriodPickerStore.State>()
        store.states(observer { states += it })
        store.labels(observer { it.handle(router) })

        store.accept(
            Intent.ClosePeriodPicker(
                dateFrom,
                dateTo
            )
        )

        verify(router).closePeriodPicker(SbisPeriodPickerRange(dateFrom, dateTo))
        assertEquals(dateFrom.timeInMillis, states.last().startPeriod?.timeInMillis ?: -1)
        assertEquals(dateTo.timeInMillis, states.last().endPeriod?.timeInMillis ?: -1)
    }
}