package ru.tensor.sbis.design.compact_period_picker

import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.rx.observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Action
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Executor
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Message
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStoreFactory
import ru.tensor.sbis.design.period_picker.view.utils.CalendarDayRange
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import java.util.Calendar
import java.util.GregorianCalendar
import com.arkivanov.mvikotlin.core.store.Executor as MviExecutor

/**
 * Тестирование [PeriodPickerStore].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class CompactPeriodPickerStoreTest {

    private val testMainDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
    private val customTheme: (Calendar) -> SbisPeriodPickerDayCustomTheme = { SbisPeriodPickerDayCustomTheme() }

    private val startCalendarDate = GregorianCalendar(2023, 4, 1)
    private val endCalendarDate = Calendar.getInstance().apply {
        add(Calendar.MONTH, 4)
        dayOfMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
        removeTime()
    }

    private val startSelectionDate = GregorianCalendar(2023, 8, 10)
    private val endSelectionDate = GregorianCalendar(2023, 8, 19)

    private val date = GregorianCalendar(2023, 8, 25)
    private val dateFrom = GregorianCalendar(2023, 8, 1)
    private val dateTo = GregorianCalendar(2023, 8, 30)

    private val dayCountersRepository: SbisPeriodPickerDayCountersRepository = mock {
        on { getDayCountersFlow(any()) }.doReturn(
            flowOf(mapOf(date to 2))
        )
    }

    private val dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory = mock {
        on { createSbisPeriodPickerDayCountersRepository() }.doReturn(dayCountersRepository)
    }

    private val markerType = MarkerType.NO_MARKER
    private val storage: CalendarStorage = CalendarStorage()
    private val displayedRange = SbisPeriodPickerRange()

    private val repository: CalendarStorageRepository = mockk {
        coEvery { getCalendarStorage(any(), any(), any(), any(), any(), any(), any()) } returns storage
        coEvery { addDataToStorage(any(), any(), any()) } returns storage
    }

    private val executor = Executor(
        repository,
        markerType = markerType,
        true,
        dayCountersRepository,
        displayedRange,
        null,
        false,
        customTheme
    )

    private lateinit var store: PeriodPickerStore

    private val callbacks: MviExecutor.Callbacks<State, Message, Label> = mock {
        on { state }.doReturn(State())
    }

    @Before
    fun setup() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(testMainDispatcher)

        store = PeriodPickerStoreFactory(
            DefaultStoreFactory(),
            repository,
            startSelectionDate,
            endSelectionDate,
            null,
            null,
            SbisPeriodPickerSelectionType.Range,
            SbisPeriodPickerDayType.Simple,
            displayedRange,
            null,
            isBottomPosition = false,
            isCompact = true,
            isFragment = false,
            null,
            customTheme,
            dayCountersFactory
        ).create(StateKeeperDispatcher())
    }

    @After
    fun teardown() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
    }

    @Test
    fun `When period picker screen is created then calendar storage is loaded and selected period is updated`() =
        runTest {
            executor.init(callbacks)
            executor.executeAction(
                Action.LoadCalendarStorage(
                    displayedRange,
                    markerType,
                    true,
                    null,
                    customTheme,
                    null,
                    false
                )
            )

            coVerify {
                repository.getCalendarStorage(
                    startCalendarDate,
                    endCalendarDate,
                    displayedRange,
                    true,
                    markerType,
                    null,
                    customTheme
                )
            }

            val state = store.state
            assertEquals(0, state.calendarStorage.dayGrid.size)
            assertEquals(startSelectionDate, state.startPeriod)
            assertEquals(endSelectionDate, state.endPeriod)
        }

    @Test
    @DisplayName(
        "When current day is not a part of calendar displayed range and period picker screen is created " +
            "then calendar storage is loaded and selected period is updated"
    )
    fun loadCalendar() =
        runTest {
            val range = SbisPeriodPickerRange(
                GregorianCalendar(2021, 8, 6).removeTime(),
                GregorianCalendar(2022, 8, 6).removeTime()
            )

            val store = PeriodPickerStoreFactory(
                DefaultStoreFactory(),
                repository,
                null,
                null,
                null,
                null,
                SbisPeriodPickerSelectionType.Range,
                SbisPeriodPickerDayType.Simple,
                range,
                null,
                isBottomPosition = false,
                isCompact = true,
                isFragment = false,
                null,
                customTheme,
                dayCountersFactory
            ).create(StateKeeperDispatcher())

            val executor = Executor(
                repository,
                markerType = markerType,
                true,
                dayCountersRepository,
                range,
                null,
                false,
                customTheme
            )

            executor.init(callbacks)

            val startCalendarDate = GregorianCalendar(2021, 8, 1)
            val endCalendarDate = range.start.apply {
                add(Calendar.MONTH, 4)
                dayOfMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
                removeTime()
            }

            coVerify {
                repository.getCalendarStorage(
                    startCalendarDate,
                    endCalendarDate,
                    range,
                    true,
                    markerType,
                    null,
                    customTheme
                )
            }

            val state = store.state
            assertEquals(0, state.calendarStorage.dayGrid.size)
            assertNull(state.startPeriod)
            assertNull(state.endPeriod)
        }

    @Test
    fun `When day is selected then startPeriod is updated and endPeriod is null`() =
        runTest {
            val states = ArrayList<State>()
            store.states(observer { states += it })

            store.accept(Intent.SelectDay(date))

            assertEquals(date, states.last().startPeriod)
            assertNull(states.last().endPeriod)
        }

    @Test
    fun `When period is selected then startPeriod and endPeriod are updated`() =
        runTest {
            val states = ArrayList<State>()
            store.states(observer { states += it })

            store.accept(Intent.SelectDay(dateFrom))

            assertEquals(dateFrom, states.last().startPeriod)
            assertNotEquals(dateFrom, states.last().endPeriod)

            store.accept(Intent.SelectDay(dateTo))

            assertEquals(dateTo, states.last().endPeriod)
            assertNotEquals(dateTo, states.last().startPeriod)
        }

    @Test
    fun `When month is selected then startPeriod and endPeriod are updated`() =
        runTest {
            val states = ArrayList<State>()
            store.states(observer { states += it })

            store.accept(Intent.SelectMonthPeriod(dateFrom, dateTo))

            assertEquals(dateFrom, states.last().startPeriod)
            assertEquals(dateTo, states.last().endPeriod)
        }

    @Test
    fun `When counters are updated then state is updated`() =
        runTest {
            val states = ArrayList<State>()
            store.states(observer { states += it })

            store.accept(Intent.UpdateCounters(CalendarDayRange(dateFrom, dateTo), testMainDispatcher))

            assertEquals(1, states.last().counters?.size)
        }

    @Test
    fun `When selection resets then startPeriod and endPeriod are null`() =
        runTest {
            val states = ArrayList<State>()
            store.states(observer { states += it })

            store.accept(Intent.ResetSelection)

            assertNull(states.last().startPeriod)
            assertNull(states.last().endPeriod)
        }

    @Test
    fun `When new data is reloaded then calendar is updated`() =
        runTest {
            val states = ArrayList<State>()

            store.states(observer { states += it })

            store.accept(Intent.ReloadCalendar(false))

            coVerify {
                repository.getCalendarStorage(
                    startCalendarDate,
                    endCalendarDate,
                    displayedRange,
                    true,
                    markerType,
                    null,
                    customTheme
                )
            }
            coVerify { repository.addDataToStorage(storage, storage, false) }

            assertEquals(0, states.last().calendarStorage.dayGrid.size)
        }

    @Test
    fun `When calendar storage is requested then calendar is generated`() = runTest {
        val spyRepository = spy(CalendarStorageRepository())

        spyRepository.getCalendarStorage(
            dateFrom,
            dateTo,
            SbisPeriodPickerRange(),
            true,
            MarkerType.NO_MARKER,
            null,
            customTheme
        )

        verify(spyRepository, times(1)).generateCalendar(
            dateFrom,
            dateTo,
            SbisPeriodPickerRange(),
            true,
            MarkerType.NO_MARKER,
            null,
            customTheme
        )
    }

    @Test
    fun `When calendar is reloaded then new data is added to calendar storage`() = runTest {
        val repository = CalendarStorageRepository(testMainDispatcher)
        val spyRepository = spy(repository)
        val spyStorage = spy(CalendarStorage())
        val storage = CalendarStorage()

        spyRepository.addDataToStorage(
            spyStorage,
            storage,
            true
        )

        verify(spyStorage, times(1)).addDataToStorage(any(), any())
    }
}