package ru.tensor.sbis.design.short_period_picker

import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.getShortPeriodPickerSelection
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToHalfYearResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToMonthResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToQuarterResId
import java.util.GregorianCalendar

/**
 * Тестирование методов вспомогательного класса.
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [28])
class ShortPeriodPickerUtilsTest {

    private val start = GregorianCalendar(2024, 0, 1)
    private val monthRange = 0..11
    private val incorrectMonth = 12

    @Test
    fun `Set month selection`() {
        val end = GregorianCalendar(2024, 0, 31)
        val selection = getShortPeriodPickerSelection(start, end)

        assertEquals(selection.selectionType, SelectionType.MONTH)
    }

    @Test
    fun `Set quarter selection`() {
        val end = GregorianCalendar(2024, 2, 31)
        val selection = getShortPeriodPickerSelection(start, end)

        assertEquals(selection.selectionType, SelectionType.QUARTER)
    }

    @Test
    fun `Set half year selection`() {
        val end = GregorianCalendar(2024, 5, 30)
        val selection = getShortPeriodPickerSelection(start, end)

        assertEquals(selection.selectionType, SelectionType.HALF_YEAR)
    }

    @Test
    fun `Set year selection`() {
        val end = GregorianCalendar(2024, 11, 31)
        val selection = getShortPeriodPickerSelection(start, end)

        assertEquals(selection.selectionType, SelectionType.YEAR)
    }

    @Test
    fun `When month is from 0 to 11 then mapMonthToMonthResId method returns string resource`() {
        monthRange.forEach {
            val month = mapMonthToMonthResId(it)
            assert(month != ResourcesCompat.ID_NULL)
        }
    }

    @Test
    fun `When month is over 11 then mapMonthToMonthResId method returns illegal argument exception`() {
        assertThrows<IllegalArgumentException> { mapMonthToMonthResId(incorrectMonth) }
    }

    @Test
    fun `When month is from 0 to 11 then mapMonthToQuarterResId method returns string resource`() {
        monthRange.forEach {
            val month = mapMonthToQuarterResId(it)
            assert(month != ResourcesCompat.ID_NULL)
        }
    }

    @Test
    fun `When month is over 11 then mapMonthToQuarterResId method returns illegal argument exception`() {
        assertThrows<IllegalArgumentException> { mapMonthToQuarterResId(incorrectMonth) }
    }

    @Test
    fun `When month is from 0 to 11 then mapMonthToHalfYearResId method returns string resource`() {
        monthRange.forEach {
            val month = mapMonthToHalfYearResId(it)
            assert(month != ResourcesCompat.ID_NULL)
        }
    }

    @Test
    fun `When month is over 11 then mapMonthToHalfYearResId method returns illegal argument exception`() {
        assertThrows<IllegalArgumentException> { mapMonthToHalfYearResId(incorrectMonth) }
    }
}