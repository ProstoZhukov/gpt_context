package ru.tensor.sbis.design.short_period_picker

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams

/**
 * Тестирование визуальных праметров [SbisShortPeriodPickerVisualParams].
 *
 * @author mb.kruglova
 */
@RunWith(JUnit4::class)
class SbisShortPeriodPickerVisualParamsTest {

    @Test
    fun `For default SbisShortPeriodPickerVisualParams isYearMode returns false`() {
        val params = SbisShortPeriodPickerVisualParams()

        assert(!params.isYearMode())
    }

    @Test
    fun `When chooseYears is true then isYearMode returns true too`() {
        val params = SbisShortPeriodPickerVisualParams(
            chooseYears = true
        )

        assert(params.isYearMode())
    }

    @Test
    fun `When all params are true then isYearMode returns false`() {
        val params = SbisShortPeriodPickerVisualParams(
            arrowVisible = true,
            chooseHalfYears = true,
            chooseMonths = true,
            chooseQuarters = true,
            chooseYears = true
        )

        assert(!params.isYearMode())
    }
}