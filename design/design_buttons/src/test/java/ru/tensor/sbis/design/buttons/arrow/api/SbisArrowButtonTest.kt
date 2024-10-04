package ru.tensor.sbis.design.buttons.arrow.api

import android.app.Activity
import android.os.Build
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.buttons.SbisArrowButton
import ru.tensor.sbis.design.buttons.arrow.model.ArrowButtonStyleSet
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonBackgroundType
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Тесты API компонента [SbisArrowButton].
 *
 * @author mb.kruglova
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisArrowButtonTest {

    private lateinit var button: SbisArrowButton

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        button = SbisArrowButton(activity)
    }

    @Test
    fun `SbisArrowButton has left arrow mode by default`() {
        Assert.assertEquals(button.mode, HorizontalPosition.LEFT)
    }

    @Test
    fun `Set right arrow mode`() {
        button.mode = HorizontalPosition.RIGHT
        Assert.assertEquals(button.mode, HorizontalPosition.RIGHT)
    }

    @Test
    fun `SbisArrowButton has medium size by default`() {
        Assert.assertEquals(button.size, SbisRoundButtonSize.S)
    }

    @Test
    fun `Set large size`() {
        button.size = SbisRoundButtonSize.L
        Assert.assertEquals(button.size, SbisRoundButtonSize.L)
    }

    @Test
    fun `When FILLED_ON_TAP type and PALE style are set, then get FILLED_ON_TAP_PALE style set`() {
        Assert.assertEquals(
            ArrowButtonStyleSet.getStyleSet(SbisArrowButtonBackgroundType.FILLED_ON_TAP, SbisArrowButtonStyle.PALE),
            ArrowButtonStyleSet.FILLED_ON_TAP_PALE
        )
    }

    @Test
    fun `When FILLED type and PALE style are set, then get FILLED_PALE style set`() {
        Assert.assertEquals(
            ArrowButtonStyleSet.getStyleSet(SbisArrowButtonBackgroundType.FILLED, SbisArrowButtonStyle.PALE),
            ArrowButtonStyleSet.FILLED_PALE
        )
    }

    @Test
    fun `When FILLED_ON_TAP type and DEFAULT style are set, then get FILLED_DEFAULT style set`() {
        Assert.assertEquals(
            ArrowButtonStyleSet.getStyleSet(SbisArrowButtonBackgroundType.FILLED_ON_TAP, SbisArrowButtonStyle.DEFAULT),
            ArrowButtonStyleSet.FILLED_DEFAULT
        )
    }

    @Test
    fun `When FILLED type and DEFAULT style are set, then get FILLED_ON_TAP_PALE style set`() {
        Assert.assertEquals(
            ArrowButtonStyleSet.getStyleSet(SbisArrowButtonBackgroundType.FILLED, SbisArrowButtonStyle.DEFAULT),
            ArrowButtonStyleSet.FILLED_DEFAULT
        )
    }

}