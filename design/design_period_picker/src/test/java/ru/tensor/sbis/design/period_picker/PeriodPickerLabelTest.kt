package ru.tensor.sbis.design.period_picker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerRouter
import java.util.GregorianCalendar

/**
 * Тестирование сайд-эффектов [Label] в Большом выборе периода.
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
class PeriodPickerLabelTest {

    private val router: PeriodPickerRouter = mock()

    private val startPeriod = GregorianCalendar(2023, 8, 10)
    private val endPeriod = GregorianCalendar(2023, 8, 19)

    @Test
    fun `When NavigateToMonthMode label is called then router calls openMonthMode`() = runTest {
        Label.NavigateToMonthMode(startPeriod, endPeriod).handle(router)
        verify(router).openMonthMode(startPeriod, endPeriod)
    }

    @Test
    fun `When NavigateToYearMode label is called then router calls openYearMode`() = runTest {
        Label.NavigateToYearMode(startPeriod, endPeriod).handle(router)
        verify(router).openYearMode(startPeriod, endPeriod)
    }

    @Test
    fun `When ResetSelection label is called then router calls resetSelection`() = runTest {
        Label.ResetSelection.handle(router)
        verify(router).resetSelection()
    }

    @Test
    fun `When SetPresetSelection label is called then router calls setPresetSelection`() = runTest {
        Label.SetPresetSelection(startPeriod, endPeriod).handle(router)
        verify(router).setPresetSelection(startPeriod, endPeriod)
    }

    @Test
    fun `When UpdateSelection label is called then router calls updateSelection`() = runTest {
        val tag = SbisPeriodPickerMode.MONTH.tag
        Label.UpdateSelection(tag).handle(router)
        verify(router).updateSelection(tag)
    }

    @Test
    fun `When ClosePeriodPicker label is called then router calls closePeriodPicker`() = runTest {
        val range = SbisPeriodPickerRange(startPeriod, endPeriod)
        Label.ClosePeriodPicker(range).handle(router)
        verify(router).closePeriodPicker(range)
    }
}