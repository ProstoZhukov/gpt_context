package ru.tensor.sbis.design.buttons.keyboard.api

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon.Icon
import ru.tensor.sbis.design.buttons.SbisKeyboardButton
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.utils.style.PRESSED_COLOR_STATES
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonItemType
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonSize
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardIcon
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleActionType
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleInputType
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleMainActionType

/**
 * Тесты API компонента [SbisKeyboardButton].
 *
 * @author ra.geraskin
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisKeyboardButtonTest {

    private lateinit var button: SbisKeyboardButton

    companion object {
        const val titleIcon = "2"
        val mobileIcon = Icon.smi_Enter
        val defaultColor = ColorStateList.valueOf(Color.MAGENTA)
    }

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        button = SbisKeyboardButton(activity)
    }

    @Test
    fun `SbisKeyboardButton has size L by default`() {
        assertEquals(button.size, SbisKeyboardButtonSize.L)
    }

    @Test
    fun `When button size is L, then title-icon size is L too`() {
        button.size = SbisKeyboardButtonSize.L
        button.keyboardIcon = SbisKeyboardIcon(titleIcon, button.size.iconSize, defaultColor)
        assertEquals(button.keyboardIcon.title!!.size, SbisKeyboardButtonSize.L.iconSize.textIconSize)
    }

    @Test
    fun `When button size is L, then mobile-icon size is L too`() {
        button.size = SbisKeyboardButtonSize.L
        button.keyboardIcon = SbisKeyboardIcon(mobileIcon, button.size.iconSize, defaultColor)
        assertEquals(button.keyboardIcon.icon!!.size, SbisKeyboardButtonSize.L.iconSize.mobileIconSize)
    }

    @Test
    fun `SbisKeyboardButton is enabled by default`() {
        assertEquals(button.state, SbisButtonState.ENABLED)
    }

    @Test
    fun `Set disabled state`() {
        button.state = SbisButtonState.DISABLED
        assertEquals(button.state, SbisButtonState.DISABLED)
    }

    @Test
    fun `When set not null title, then icon is null`() {
        button.keyboardIcon = SbisKeyboardIcon(titleIcon, button.size.iconSize, defaultColor)
        assertNull(button.keyboardIcon.icon)
    }

    @Test
    fun `When set not null icon, then title is null`() {
        button.keyboardIcon = SbisKeyboardIcon(mobileIcon, button.size.iconSize, defaultColor)
        assertNull(button.keyboardIcon.title)
    }

    @Test
    fun `When flag needSetupShadow is true, then button elevation is not 0`() {
        button.needSetupShadow = true
        assertTrue(button.elevation != 0f)
    }

    @Test
    fun `When flag needSetupShadow is false, then button elevation is 0`() {
        button.needSetupShadow = false
        assertTrue(button.elevation == 0f)
    }

    @Test
    fun `When type is INPUT, then button style is applied from getStyleInputType method`() {
        button.itemType = SbisKeyboardButtonItemType.INPUT
        assertTrue(hasButtonStylesSame(button.style as SbisButtonCustomStyle, getStyleInputType(button.context)))
    }

    @Test
    fun `When type is ACTION, then button style is applied from getStyleActionType method`() {
        button.itemType = SbisKeyboardButtonItemType.ACTION
        assertTrue(hasButtonStylesSame(button.style as SbisButtonCustomStyle, getStyleActionType(button.context)))
    }

    @Test
    fun `When type is ACTION_MAIN, then button style is applied from getStyleMainActionType method`() {
        button.itemType = SbisKeyboardButtonItemType.ACTION_MAIN
        assertTrue(hasButtonStylesSame(button.style as SbisButtonCustomStyle, getStyleMainActionType(button.context)))
    }

    @Test
    fun `When set icon via keyboardIcon property, then model icon is also set`() {
        button.keyboardIcon = SbisKeyboardIcon(titleIcon, button.size.iconSize, defaultColor)
        assertEquals(button.keyboardIcon.icon, button.model.icon)
    }

    @Test
    fun `When set title via keyboardIcon property, then model title is also set`() {
        button.keyboardIcon = SbisKeyboardIcon(titleIcon, button.size.iconSize, defaultColor)
        assertEquals(button.keyboardIcon.title, button.model.title)
    }

    private fun hasButtonStylesSame(style1: SbisButtonCustomStyle, style2: SbisButtonCustomStyle) =
        hasBackgroundColorsSame(style1, style2) &&
            style1.iconStyle == style2.iconStyle &&
            style1.titleStyle == style2.titleStyle

    private fun hasBackgroundColorsSame(style1: SbisButtonCustomStyle, style2: SbisButtonCustomStyle) =
        style1.backgroundColors.getColorForState(PRESSED_COLOR_STATES[0], 0) ==
            style2.backgroundColors.getColorForState(PRESSED_COLOR_STATES[0], 1) &&
            style1.backgroundColors.getColorForState(PRESSED_COLOR_STATES[1], 0) ==
            style2.backgroundColors.getColorForState(PRESSED_COLOR_STATES[1], 1)

}