package ru.tensor.sbis.design.checkbox

import android.app.Activity
import android.graphics.Color
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxBackgroundType
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxMode
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValidationState
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Тесты API компонента [SbisCheckboxView].
 *
 * @author mb.kruglova
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisCheckboxViewTest {

    private lateinit var checkbox: SbisCheckboxView

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        checkbox = SbisCheckboxView(activity)
    }

    @Test
    fun `Checkbox has standard mode by default`() {
        assertEquals(checkbox.mode, SbisCheckboxMode.STANDARD)
    }

    @Test
    fun `Set accent mode`() {
        checkbox.mode = SbisCheckboxMode.ACCENT
        assertEquals(checkbox.mode, SbisCheckboxMode.ACCENT)
    }

    @Test
    fun `Checkbox hasn't content by default`() {
        assertTrue(checkbox.content is SbisCheckboxContent.NoContent)
    }

    @Test
    fun `Set text content`() {
        checkbox.content = SbisCheckboxContent.TextContent("abc")
        assertTrue(checkbox.content is SbisCheckboxContent.TextContent)
        val textContent = checkbox.content as SbisCheckboxContent.TextContent
        assertEquals(textContent.text, "abc")
        assertNull(textContent.color)
        assertNull(textContent.isMaxLines)
    }

    @Test
    fun `Set text content with gray color`() {
        checkbox.content = SbisCheckboxContent.TextContent("abc", Color.GRAY)
        assertTrue(checkbox.content is SbisCheckboxContent.TextContent)
        val textContent = checkbox.content as SbisCheckboxContent.TextContent
        assertEquals(textContent.text, "abc")
        assertEquals(textContent.color, Color.GRAY)
        assertNull(textContent.isMaxLines)
    }

    @Test
    fun `Set text content with gray color and maximum lines`() {
        checkbox.content = SbisCheckboxContent.TextContent("abc", Color.GRAY, true)
        assertTrue(checkbox.content is SbisCheckboxContent.TextContent)
        val textContent = checkbox.content as SbisCheckboxContent.TextContent
        assertEquals(textContent.text, "abc")
        assertEquals(textContent.color, Color.GRAY)
        assertEquals(textContent.isMaxLines, true)
    }

    @Test
    fun `Set icon content`() {
        checkbox.content = SbisCheckboxContent.IconContent("")
        assertTrue(checkbox.content is SbisCheckboxContent.IconContent)
        assertEquals((checkbox.content as SbisCheckboxContent.IconContent).iconText, "\uE61F")
    }

    @Test
    fun `Set icon content with blue color`() {
        checkbox.content = SbisCheckboxContent.IconContent("", Color.BLUE)
        assertTrue(checkbox.content is SbisCheckboxContent.IconContent)
        val iconContent = checkbox.content as SbisCheckboxContent.IconContent
        assertEquals(iconContent.iconText, "")
        assertEquals(iconContent.color, Color.BLUE)
    }

    @Test
    fun `Content is aligned to the right of the checkbox by default`() {
        assertEquals(checkbox.position, HorizontalPosition.RIGHT)
    }

    @Test
    fun `Set content alignment to the left of the checkbox`() {
        checkbox.position = HorizontalPosition.LEFT
        assertEquals(checkbox.position, HorizontalPosition.LEFT)
    }

    @Test
    fun `Checkbox size is small by default`() {
        assertEquals(checkbox.size, SbisCheckboxSize.SMALL)
    }

    @Test
    fun `Set size large`() {
        checkbox.size = SbisCheckboxSize.LARGE
        assertEquals(checkbox.size, SbisCheckboxSize.LARGE)
    }

    @Test
    fun `Checkbox value is unchecked by default`() {
        assertEquals(checkbox.value, SbisCheckboxValue.UNCHECKED)
    }

    @Test
    fun `Set checkbox value checked`() {
        checkbox.value = SbisCheckboxValue.CHECKED
        assertEquals(checkbox.value, SbisCheckboxValue.CHECKED)
    }

    @Test
    fun `Set checkbox value undefined`() {
        checkbox.value = SbisCheckboxValue.UNDEFINED
        assertEquals(checkbox.value, SbisCheckboxValue.UNDEFINED)
    }

    @Test
    fun `Checkbox preset value is null by default`() {
        assertNull(checkbox.presetValue)
    }

    @Test
    fun `Set preset value checked`() {
        checkbox.presetValue = SbisCheckboxValue.CHECKED
        assertNotNull(checkbox.presetValue)
        assertEquals(checkbox.presetValue, SbisCheckboxValue.CHECKED)
    }

    @Test
    fun `Set preset value unchecked`() {
        checkbox.presetValue = SbisCheckboxValue.UNCHECKED
        assertNotNull(checkbox.presetValue)
        assertEquals(checkbox.presetValue, SbisCheckboxValue.UNCHECKED)
    }

    @Test
    fun `Set preset value undefined`() {
        checkbox.presetValue = SbisCheckboxValue.UNDEFINED
        assertNotNull(checkbox.presetValue)
        assertEquals(checkbox.presetValue, SbisCheckboxValue.UNDEFINED)
    }

    @Test
    fun `Checkbox doesn't use vertical offset by default`() {
        assertEquals(checkbox.useVerticalOffset, false)
    }

    @Test
    fun `Set checkbox vertical offset true`() {
        checkbox.useVerticalOffset = true
        assertTrue(checkbox.useVerticalOffset)
    }

    @Test
    fun `Checkbox background type is filled by default`() {
        assertEquals(checkbox.backgroundType, SbisCheckboxBackgroundType.FILLED)
    }

    @Test
    fun `Set background type outlined`() {
        checkbox.backgroundType = SbisCheckboxBackgroundType.OUTLINED
        assertEquals(checkbox.backgroundType, SbisCheckboxBackgroundType.OUTLINED)
    }

    @Test
    fun `Set validation state Default`() {
        checkbox.validationState = SbisCheckboxValidationState.Default()
        assertEquals(checkbox.validationState, SbisCheckboxValidationState.Default())
    }

    @Test
    fun `Set validation state Error`() {
        val validationStateComment = "Test"
        checkbox.validationState = SbisCheckboxValidationState.Error(validationStateComment)
        assertEquals(checkbox.validationState, SbisCheckboxValidationState.Error(validationStateComment))
    }

}