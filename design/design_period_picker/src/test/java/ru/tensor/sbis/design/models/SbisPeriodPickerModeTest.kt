package ru.tensor.sbis.design.models

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.Companion.getMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.Companion.getOppositeMode

/**
 * Тестирование [SbisPeriodPickerMode].
 *
 * @author mb.kruglova
 */
@RunWith(JUnit4::class)
class SbisPeriodPickerModeTest {

    @Test
    fun `When toggle button is checked then mode is MONTH`() {
        val mode = getMode(true)

        assert(mode == SbisPeriodPickerMode.MONTH)
    }

    @Test
    fun `When toggle button is not checked then mode is YEAR`() {
        val mode = getMode(false)

        assert(mode == SbisPeriodPickerMode.YEAR)
    }

    @Test
    fun `When current mode is MONTH then opposite mode is YEAR`() {
        val mode = getOppositeMode(SbisPeriodPickerMode.MONTH.tag)

        assert(mode == SbisPeriodPickerMode.YEAR)
    }

    @Test
    fun `When current mode is YEAR then opposite mode is MONTH`() {
        val mode = getOppositeMode(SbisPeriodPickerMode.YEAR.tag)

        assert(mode == SbisPeriodPickerMode.MONTH)
    }
}