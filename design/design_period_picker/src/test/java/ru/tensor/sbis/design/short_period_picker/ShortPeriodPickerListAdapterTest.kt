package ru.tensor.sbis.design.short_period_picker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerListAdapter
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.getPeriodPickerItems
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.year

/**
 * Тесты адаптера [ShortPeriodPickerListAdapter].
 *
 * @author mb.kruglova
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(AndroidJUnit4::class)
class ShortPeriodPickerListAdapterTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private var listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit = { _, _, _ -> }
    private var adapter = ShortPeriodPickerListAdapter(listener, null, true, SbisPeriodPickerRange())

    private var startCalendar = 2024
    private var endCalendar = 2025

    @Before
    fun setup() {
        context.theme.applyStyle(ru.tensor.sbis.design.R.style.AppTheme, false)
    }

    @Test
    fun `When visual params are default then adapter doesn't have items`() {
        val visualParams = SbisShortPeriodPickerVisualParams()
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        assertEquals(adapter.itemCount, 0)
    }

    @Test
    fun `When visual params are set to choose any quanta and calendar is limited by default values then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseHalfYears = true,
            chooseQuarters = true,
            chooseYears = true
        )

        val list = getPeriodPickerItems(visualParams, MIN_DATE.year, MAX_DATE.year)
        adapter.updateData(list)

        val itemCount = getItemCount(MIN_DATE.year, MAX_DATE.year)

        assertEquals(adapter.itemCount, itemCount)
    }

    @Test
    fun `When visual params are set to choose any quanta then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseHalfYears = true,
            chooseQuarters = true,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val monthItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)

        assertEquals(monthItem.year, startCalendar)
        assertTrue(monthItem is ShortPeriodPickerItem.MonthItem)
        assertTrue((monthItem as ShortPeriodPickerItem.MonthItem).isQuarterVisible)
        assertTrue(monthItem.isHalfYearVisible)
    }

    @Test
    fun `When visual params are set not to choose half year quantum then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseQuarters = true,
            chooseHalfYears = false,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val monthItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)

        assertEquals(monthItem.year, startCalendar)
        assertTrue(monthItem is ShortPeriodPickerItem.MonthItem)
        assertTrue((monthItem as ShortPeriodPickerItem.MonthItem).isQuarterVisible)
        assertFalse(monthItem.isHalfYearVisible)
    }

    @Test
    fun `When visual params are set to choose month or year then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseQuarters = false,
            chooseHalfYears = false,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val monthItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)

        assertEquals(monthItem.year, startCalendar)
        assertTrue(monthItem is ShortPeriodPickerItem.MonthItem)
        assertFalse((monthItem as ShortPeriodPickerItem.MonthItem).isQuarterVisible)
        assertFalse(monthItem.isHalfYearVisible)
    }

    @Test
    fun `When visual params are set not to choose quarter then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseQuarters = false,
            chooseHalfYears = true,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val monthItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)

        assertEquals(monthItem.year, startCalendar)
        assertTrue(monthItem is ShortPeriodPickerItem.MonthItem)
        assertFalse((monthItem as ShortPeriodPickerItem.MonthItem).isQuarterVisible)
        assertTrue(monthItem.isHalfYearVisible)
    }

    @Test
    fun `When visual params are set to choose month then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = true,
            chooseQuarters = false,
            chooseHalfYears = false,
            chooseYears = false
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val monthItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertFalse(yearItem.isYearVisible)

        assertEquals(monthItem.year, startCalendar)
        assertTrue(monthItem is ShortPeriodPickerItem.MonthItem)
        assertFalse((monthItem as ShortPeriodPickerItem.MonthItem).isQuarterVisible)
        assertFalse(monthItem.isHalfYearVisible)
    }

    @Test
    fun `When visual params are set to choose quarter then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = true,
            chooseHalfYears = false,
            chooseYears = false
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val quarterItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertFalse(yearItem.isYearVisible)

        assertEquals(quarterItem.year, startCalendar)
        assertTrue(quarterItem is ShortPeriodPickerItem.QuarterItem)
        assertFalse((quarterItem as ShortPeriodPickerItem.QuarterItem).isHalfYearVisible)
    }

    @Test
    fun `When visual params are set to choose quarter or half year then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = true,
            chooseHalfYears = true,
            chooseYears = false
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val quarterItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertFalse(yearItem.isYearVisible)

        assertEquals(quarterItem.year, startCalendar)
        assertTrue(quarterItem is ShortPeriodPickerItem.QuarterItem)
        assertTrue((quarterItem as ShortPeriodPickerItem.QuarterItem).isHalfYearVisible)
    }

    @Test
    fun `When visual params are set to choose half year or year then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = false,
            chooseHalfYears = true,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val halfYearItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)

        assertEquals(halfYearItem.year, startCalendar)
        assertTrue(halfYearItem is ShortPeriodPickerItem.HalfYearItem)
    }

    @Test
    fun `When visual params are set to choose half year then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = false,
            chooseHalfYears = true,
            chooseYears = false
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)
        val halfYearItem = adapter.getItemByPosition(1)
        val itemCount = getItemCount(startCalendar, endCalendar)

        assertEquals(adapter.itemCount, itemCount)

        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertTrue((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertFalse(yearItem.isYearVisible)

        assertEquals(halfYearItem.year, startCalendar)
        assertTrue(halfYearItem is ShortPeriodPickerItem.HalfYearItem)
    }

    @Test
    fun `When visual params are set to choose year then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = false,
            chooseHalfYears = false,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val yearItem = adapter.getItemByPosition(0)

        assertEquals(adapter.itemCount, 2)
        assertEquals(yearItem.year, startCalendar)
        assertTrue(yearItem is ShortPeriodPickerItem.YearItem)
        assertFalse((yearItem as ShortPeriodPickerItem.YearItem).isHeader)
        assertTrue(yearItem.isYearVisible)
    }

    @Test
    fun `When visual params are set to choose years then adapter has items`() {
        val visualParams = SbisShortPeriodPickerVisualParams(
            chooseMonths = false,
            chooseQuarters = false,
            chooseHalfYears = false,
            chooseYears = true
        )
        val list = visualParams.getPeriodPickerItems()
        adapter.updateData(list)

        val pos = adapter.getLastPosition()
        assertEquals(pos, list.size - 1)
    }

    private fun getItemCount(start: Int, end: Int) = (end - start + 1) * 2

    private fun SbisShortPeriodPickerVisualParams.getPeriodPickerItems() =
        getPeriodPickerItems(this, startCalendar, endCalendar)
}