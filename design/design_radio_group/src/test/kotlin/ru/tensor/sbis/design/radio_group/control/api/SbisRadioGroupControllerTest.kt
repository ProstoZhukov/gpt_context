package ru.tensor.sbis.design.radio_group.control.api

import android.content.Context
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.radio_group.R
import ru.tensor.sbis.design.radio_group.SbisRadioGroupView
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutHorizontalStrategy
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutView
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupValidationDrawer
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupContent
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupItem
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupValidationStatus
import ru.tensor.sbis.design.radio_group.item.SbisRadioGroupItemView
import ru.tensor.sbis.design.R as RDesign


/**
 * Тестовый класс для [SbisRadioGroupController].
 *
 * @author ps.smirnyh
 */
@RunWith(RobolectricTestRunner::class)
class SbisRadioGroupControllerTest {

    private lateinit var radioGroupController: SbisRadioGroupController
    private lateinit var radioGroupViewMock: SbisRadioGroupView
    private lateinit var radioGroupLayoutViewMock: RadioGroupLayoutView
    private lateinit var validationDrawerMock: RadioGroupValidationDrawer
    private val contextMock: Context = ApplicationProvider.getApplicationContext()
    private val listViews = mutableListOf<SbisRadioGroupItemView>()

    @Before
    fun setUp() {
        contextMock.theme.applyStyle(RDesign.style.AppGlobalTheme, true)
        val styleHolder = RadioGroupStyleHolder()
        validationDrawerMock = RadioGroupValidationDrawer(styleHolder)
        radioGroupLayoutViewMock = spyk(RadioGroupLayoutView(contextMock, styleHolder, validationDrawerMock)) {
            val itemViewSlot = slot<SbisRadioGroupItemView>()
            val enabledSlot = slot<Boolean>()
            val indexChildSlot = slot<Int>()
            every { getChildAt(capture(indexChildSlot)) } answers {
                listViews[indexChildSlot.captured]
            }
            justRun { isEnabled = capture(enabledSlot) }
            every { isEnabled } answers {
                enabledSlot.captured
            }
            every {
                addView(capture(itemViewSlot))
            } answers {
                listViews.add(itemViewSlot.captured)
            }
            every { childCount } answers {
                listViews.size
            }
        }
        radioGroupViewMock = mockk {
            every { radioGroupLayoutView } returns radioGroupLayoutViewMock
            every { context } returns contextMock
        }
        radioGroupController = SbisRadioGroupController(styleHolder)
        radioGroupController.attach(contextMock, null, 0, R.style.SbisRadioGroupDefaultTheme, radioGroupViewMock)
    }

    @Test
    fun `When set new items then views are created by on passed models`() {
        val model = SbisRadioGroupItem("1", SbisRadioGroupContent.Default("Test"))
        radioGroupController.items = listOf(model)
        val view = listViews[0]

        assertEquals(model.id, view.itemId)
        assertEquals(model.content, view.content)
        assertEquals(model.readOnly, !view.isEnabled)
        assertEquals(model.parentId, view.parentItem?.itemId)
    }

    @Test
    fun `When set list of items then created same number of views`() {
        val listModels = listOf(
            SbisRadioGroupItem("1", SbisRadioGroupContent.Default("")),
            SbisRadioGroupItem("2", SbisRadioGroupContent.Default("")),
            SbisRadioGroupItem("3", SbisRadioGroupContent.Default("")),
        )
        radioGroupController.items = listModels

        assertEquals(listModels.size, listViews.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When set items with same id then throw exception`() {
        val listModels = listOf(
            SbisRadioGroupItem("1", SbisRadioGroupContent.Default("")),
            SbisRadioGroupItem("2", SbisRadioGroupContent.Default("")),
            SbisRadioGroupItem("1", SbisRadioGroupContent.Default("")),
        )
        radioGroupController.items = listModels
    }

    @Test
    fun `When set left title position then content layout offset equal 0`() {
        val xPositionSlot = slot<Int>()
        val mockContent = spyk(SbisRadioGroupContent.Default("")) {
            justRun { layout(capture(xPositionSlot), any()) }
        }
        val listModels = listOf(

            SbisRadioGroupItem("3", mockContent),
        )
        radioGroupController.items = listModels

        radioGroupController.titlePosition = SbisRadioGroupTitlePosition.LEFT
        listViews[0].run {
            measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
            layout(0, 0, 0, 0)
        }


        assertTrue(xPositionSlot.captured == 0)
    }

    @Test
    fun `When set right title position then content layout offset not equal 0`() {
        val xPositionSlot = slot<Int>()
        val mockContent = spyk(SbisRadioGroupContent.Default("")) {
            justRun { layout(capture(xPositionSlot), any()) }
        }
        val listModels = listOf(

            SbisRadioGroupItem("3", mockContent),
        )
        radioGroupController.items = listModels

        radioGroupController.titlePosition = SbisRadioGroupTitlePosition.RIGHT
        listViews[0].run {
            measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
            layout(0, 0, 0, 0)
        }

        assertFalse(xPositionSlot.captured == 0)
    }

    @Test
    fun `When set new multiline value then layout view must be relayout`() {
        radioGroupController.orientation = SbisRadioGroupOrientation.HORIZONTAL
        clearMocks(
            radioGroupLayoutViewMock,
            answers = false,
            childMocks = false,
            verificationMarks = false,
            exclusionRules = false
        )
        radioGroupController.multiline = false

        verify(exactly = 1) { radioGroupLayoutViewMock.requestLayout() }
    }

    @Test
    fun `When set same multiline and orientation equal vertical them do nothing`() {
        radioGroupController.multiline = false

        verify(exactly = 0) { radioGroupLayoutViewMock.requestLayout() }
    }

    @Test
    fun `When set readonly equal true then layout view is disabled`() {
        radioGroupController.readOnly = true

        assertFalse(radioGroupLayoutViewMock.isEnabled)
    }

    @Test
    fun `When set selectedKey then view with same key is selected`() {
        radioGroupController.items = listOf(
            SbisRadioGroupItem("1", SbisRadioGroupContent.Default("First level")),
            SbisRadioGroupItem("2", SbisRadioGroupContent.Default("First level"))
        )

        radioGroupController.selectedKey = "1"

        assertTrue(listViews.first { it.itemId == radioGroupController.selectedKey }.isSelected)
        assertEquals(1, listViews.count { it.isSelected })
    }

    @Test
    fun `When set selectedKey and this is nested item then view with same key and all parent views are selected`() {
        radioGroupController.items = listOf(
            SbisRadioGroupItem(
                "1", SbisRadioGroupContent.Default("First level"), children = listOf(
                    SbisRadioGroupItem("1.1", SbisRadioGroupContent.Default("Second level")),
                    SbisRadioGroupItem("1.2", SbisRadioGroupContent.Default("Second level")),
                )
            ),
            SbisRadioGroupItem("2", SbisRadioGroupContent.Default("First level"))
        )

        radioGroupController.selectedKey = "1.2"

        var parent: SbisRadioGroupItemView? =
            listViews.first { it.itemId == radioGroupController.selectedKey }
        while (parent != null) {
            assertTrue(parent.isSelected)
            parent = parent.parentItem
        }
        assertEquals(2, listViews.count { it.isSelected })
    }

    @Test
    fun `When set orientation then set same layout strategy`() {
        radioGroupController.orientation = SbisRadioGroupOrientation.HORIZONTAL

        assertTrue(radioGroupLayoutViewMock.strategy is RadioGroupLayoutHorizontalStrategy)
    }

    @Test
    fun `When set validation status to INVALID then draw validation rect`() {
        val canvasMock = mockk<Canvas>(relaxed = true)
        radioGroupController.validationStatus = SbisRadioGroupValidationStatus.INVALID

        radioGroupLayoutViewMock.draw(canvasMock)
        verify { validationDrawerMock.draw(canvasMock) }
        verify {
            canvasMock.drawRoundRect(
                any(),
                radioGroupController.styleHolder.validationBorderRadius.toFloat(),
                radioGroupController.styleHolder.validationBorderRadius.toFloat(),
                any()
            )
        }
    }

    @Test
    fun `When set onSelectedKeyChanged then this call after changed selectedKey`() {
        val mockLambda = spyk<(String) -> Unit>()
        radioGroupController.items = listOf(
            SbisRadioGroupItem("1", SbisRadioGroupContent.Default("First level")),
            SbisRadioGroupItem("2", SbisRadioGroupContent.Default("First level"))
        )
        radioGroupController.onSelectedKeyChanged = mockLambda

        radioGroupController.selectedKey = "1"
        radioGroupController.selectedKey = "2"

        verify(exactly = 2) { mockLambda.invoke(any()) }
    }

}