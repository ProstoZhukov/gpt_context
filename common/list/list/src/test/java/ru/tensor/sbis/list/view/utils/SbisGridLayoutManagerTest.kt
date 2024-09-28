package ru.tensor.sbis.list.view.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.utils.BaseThemedActivity
import ru.tensor.sbis.list.view.utils.layout_manager.IsFirstGroupInSection
import ru.tensor.sbis.list.view.utils.layout_manager.ItemSpanSizeLookup
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager
import ru.tensor.sbis.list.view.utils.layout_manager.SpanCountsCalculator

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
internal class SbisGridLayoutManagerTest {

    private val activityController = Robolectric.buildActivity(BaseThemedActivity::class.java).setup()
    private val activity = activityController.get()
    private val position = 123
    private val isInLastRow = mock<(Int, SbisGridLayoutManager) -> Boolean>()
    private val isInFirstRow = mock<IsFirstGroupInSection>()
    private val screenWidthPx = 555
    private val spanCountsCalculator = mock<SpanCountsCalculator> {
        on { calculate(screenWidthPx) } doReturn 3
    }
    private val spanSize = 3
    private val customSpanSizeLookup = mock<ItemSpanSizeLookup> {
        on { getSpanSize(position) } doReturn spanSize
    }
    private val manager = SbisGridLayoutManager(
        activity,
        mock(),
        mock(),
        mock(),
        spanCountsCalculator,
        customSpanSizeLookup,
        isInFirstRow,
        isInLastRow
    ).apply {
        setViewWidth(screenWidthPx)
    }

    @Test
    fun `isInLastRow true`() {
        whenever(isInLastRow(position, manager)) doReturn true

        assertTrue(manager.isInLastGroup(position))
    }

    @Test
    fun `isInLastRow false`() {
        whenever(isInLastRow(position, manager)) doReturn false

        assertFalse(manager.isInLastGroup(position))
    }

    @Test
    fun `isInFirstRow true`() {
        whenever(isInFirstRow(position, spanSize)) doReturn true

        assertTrue(manager.isFirstGroupInSection(position))
    }

    @Test
    fun `isInFirstRow false`() {
        whenever(isInFirstRow(position, spanSize)) doReturn false

        assertFalse(manager.isFirstGroupInSection(position))
    }

    @Test
    fun updateSpanCounts() {
        whenever(spanCountsCalculator.calculate(333)) doReturn 12

        manager.setViewWidth(333)

        assertEquals(12, manager.spanCount)
    }
}