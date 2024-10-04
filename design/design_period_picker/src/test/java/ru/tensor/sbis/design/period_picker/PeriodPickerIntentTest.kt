package ru.tensor.sbis.design.period_picker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Executor
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore
import java.util.GregorianCalendar

/**
 * Тестирование намерений [Intent] в Большом выборе периода.
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
class PeriodPickerIntentTest {

    private val executor: Executor = mock()

    private val dateFrom = GregorianCalendar(2023, 8, 1)
    private val dateTo = GregorianCalendar(2023, 8, 30)

    @Test
    fun `When SwitchMode intent is called and current mode is MONTH, then calendar mode is changed on YEAR`() =
        runTest {
            val intent = Intent.SwitchMode(SbisPeriodPickerMode.MONTH, null, null)
            intent.handle(
                executor,
                PeriodPickerStore.State(
                    startInitialPeriod = null,
                    endInitialPeriod = null,
                    startPresetPeriod = null,
                    endPresetPeriod = null
                )
            )
            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When SwitchMode intent is called and current mode is YEAR, then calendar mode is changed on MONTH`() =
        runTest {
            val intent = Intent.SwitchMode(SbisPeriodPickerMode.YEAR, null, null)
            intent.handle(
                executor,
                PeriodPickerStore.State(
                    startInitialPeriod = null,
                    endInitialPeriod = null,
                    startPresetPeriod = null,
                    endPresetPeriod = null
                )
            )
            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When ResetSelection intent is called and preset period is null, then period selection is reset`() =
        runTest {
            val intent = Intent.ResetSelection
            intent.handle(
                executor,
                PeriodPickerStore.State(
                    startInitialPeriod = null,
                    endInitialPeriod = null,
                    startPresetPeriod = null,
                    endPresetPeriod = null
                )
            )
            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When ResetSelection intent is called and preset period is not null, then period selection is reset`() =
        runTest {
            val intent = Intent.ResetSelection
            intent.handle(
                executor,
                PeriodPickerStore.State(
                    startInitialPeriod = null,
                    endInitialPeriod = null,
                    startPresetPeriod = dateFrom,
                    endPresetPeriod = dateTo
                )
            )
            verify(executor).publishLabel(any())
            verify(executor).dispatchMessage(any())
        }

    @Test
    fun `When UpdateSelection intent is called, then period selection is updated`() = runTest {
        val intent = Intent.UpdateSelection(
            tag = SbisPeriodPickerMode.MONTH.tag,
            dateFrom,
            dateTo
        )
        intent.handle(
            executor,
            PeriodPickerStore.State(
                startInitialPeriod = null,
                endInitialPeriod = null,
                startPresetPeriod = null,
                endPresetPeriod = null
            )
        )
        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }

    @Test
    fun `When ClosePeriodPicker intent is called, then calendar is closed`() = runTest {
        val intent = Intent.ClosePeriodPicker(
            dateFrom,
            dateTo
        )
        intent.handle(
            executor,
            PeriodPickerStore.State(
                startInitialPeriod = null,
                endInitialPeriod = null,
                startPresetPeriod = null,
                endPresetPeriod = null
            )
        )
        verify(executor).publishLabel(any())
        verify(executor).dispatchMessage(any())
    }
}