package ru.tensor.sbis.design.compact_period_picker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.model.IntentParams
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Executor
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore
import ru.tensor.sbis.design.period_picker.view.utils.CalendarDayRange
import java.util.GregorianCalendar

/**
 * Тестирование намерений [Intent] в Компактном выборе периода.
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
class CompactPeriodPickerIntentTest {

    private val date = GregorianCalendar(2023, 8, 25)
    private val dateFrom = GregorianCalendar(2023, 8, 1)
    private val dateTo = GregorianCalendar(2023, 8, 30)
    private val year = 2024

    private val startCalendar = GregorianCalendar(2023, 0, 1)
    private val endCalendar = GregorianCalendar(2025, 11, 31)

    private val executor: Executor = mock()
    private val dayCountersRepository: SbisPeriodPickerDayCountersRepository = mock()

    private val repository: CalendarStorageRepository = mock()
    private val markerType = MarkerType.NO_MARKER

    @Test
    fun `When SelectDay intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectDay(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When SelectDay intent is called on day selection in fragment then executor updates selected period`() =
        runTest {
            val intent = Intent.SelectDay(date)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(),
                getParams(true)
            )

            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When SelectMonth intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectMonth(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectMonth intent is called and the previous period exists " +
            "then the previous period is reset and executor updates selected period"
    )
    fun selectNewPeriod() = runTest {
        val intent = Intent.SelectMonth(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                startPeriod = dateFrom,
                endPeriod = dateTo
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectMonth intent is called, start date of period is selected and selection type is MONTH" +
            "then executor updates selected period"
    )
    fun completeSelectMonth() = runTest {
        val intent = Intent.SelectMonth(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                startPeriod = dateFrom,
                endPeriod = null,
                selectionType = SelectionType.MONTH
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectMonth intent is called, start date of period is selected and selection type is changed" +
            "then the previous period is reset and executor updates selected period"
    )
    fun selectMonth() = runTest {
        val intent = Intent.SelectMonth(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                startPeriod = dateFrom,
                selectionType = SelectionType.DAY
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When SelectMonth intent is called on range selection in fragment then executor updates selected period`() =
        runTest {
            val intent = Intent.SelectMonth(date)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(
                    isSingleClick = false
                ),
                getParams()
            )

            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When SelectQuantum intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectQuantum(date, date, SelectionType.COMPLETE_SELECTION)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When SelectYear intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectYear(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectYear intent is called, start date of period is selected and selection type is changed" +
            "then the previous period is reset and executor updates selected period"
    )
    fun completeSelectYear() = runTest {
        val intent = Intent.SelectYear(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                startPeriod = dateFrom,
                selectionType = SelectionType.DAY
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectYear intent is called, start date of period is selected and selection type is YEAR" +
            "then the previous period is reset and executor updates selected period"
    )
    fun selectYear() = runTest {
        val intent = Intent.SelectYear(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                startPeriod = dateFrom,
                selectionType = SelectionType.YEAR
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When SelectYear intent is called on range selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectYear(date)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(
                isSingleClick = false
            ),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When SelectMonthPeriod intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectMonthPeriod(dateFrom, dateTo)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    @DisplayName(
        "When SelectMonthPeriod intent is called on day selection in fragment " +
            "then executor updates selected period"
    )
    fun selectMonthPeriod() =
        runTest {
            val intent = Intent.SelectMonthPeriod(dateFrom, dateTo)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(),
                getParams(true)
            )

            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When SelectPeriod intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.SelectPeriod(dateFrom, dateTo)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When UpdateYearLabel intent is called on day selection then executor updates selected period`() = runTest {
        val intent = Intent.UpdateYearLabel(year)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )

        verify(executor).publishLabel(any())
    }

    @Test
    fun `When UpdateScrollToCurrentDay intent is called on day selection then executor updates selected period`() =
        runTest {
            val intent = Intent.UpdateScrollToCurrentDay
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(),
                getParams()
            )

            verify(executor).publishLabel(any())
        }

    @Test
    fun `When UpdateCounters intent is called then executor updates counters`() = runTest {
        val range = CalendarDayRange(dateFrom, dateTo)
        val intent = Intent.UpdateCounters(range)
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )
        verify(executor, never()).dispatchMessage(any())
    }

    @Test
    fun `When ResetSelection intent is called then period selection is reset`() = runTest {
        val intent = Intent.ResetSelection
        intent.handle(
            executor,
            this,
            PeriodPickerStore.State(),
            getParams()
        )
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When ReloadCalendar intent is called and isNextPage equals true then calendar reload at the bottom`() =
        runTest {
            val intent = Intent.ReloadCalendar(true)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(),
                getParams()
            )
            verify(executor, never()).publishLabel(any())
            verify(executor, never()).dispatchMessage(any())
        }

    @Test
    fun `When ReloadCalendar intent is called and isNextPage equals false then calendar reload at the top`() =
        runTest {
            val intent = Intent.ReloadCalendar(false)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(),
                getParams()
            )
            verify(executor, never()).publishLabel(any())
            verify(executor, never()).dispatchMessage(any())
        }

    @Test
    fun `When ReloadCalendar intent is called and year is a part of loaded calendar then calendar doesn't reload`() =
        runTest {
            val intent = Intent.ReloadCalendar(false, year)
            intent.handle(
                executor,
                this,
                PeriodPickerStore.State(
                    startCalendar = startCalendar,
                    endCalendar = endCalendar
                ),
                getParams()
            )
            verify(executor, never()).publishLabel(any())
            verify(executor, never()).dispatchMessage(any())
        }

    private fun getParams(isFragment: Boolean = false) = IntentParams(
        repository,
        markerType,
        true,
        dayCountersRepository,
        SbisPeriodPickerRange(),
        null,
        isFragment = isFragment,
        dayCustomTheme = { SbisPeriodPickerDayCustomTheme() }
    )
}