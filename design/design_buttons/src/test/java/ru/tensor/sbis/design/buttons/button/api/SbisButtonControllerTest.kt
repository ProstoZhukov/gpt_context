package ru.tensor.sbis.design.buttons.button.api

import android.app.Activity
import android.view.View
import org.mockito.kotlin.*
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder

/**
 * @author ma.kolpakov
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class SbisButtonControllerTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

    private val button = spy(View(activity))

    private val controller = SbisButtonController()

    @Before
    fun setUp() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        val style = SbisButtonStyle.DEFAULT
        val globalStyleHolder = SbisButtonStyleHolder()
        controller.attach(button, null, style.buttonStyle, style.defaultButtonStyle, globalStyleHolder)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=d13efbb3-237e-4436-ba52-b4241b95fd7b
    @Test
    fun `When button created, it does not have any listeners`() {
        controller.model = controller.model.copy()

        assertFalse(button.hasOnClickListeners())
    }

    @Test
    fun `When button model changed, then click listener should not be modified`() {
        controller.model = controller.model.copy()

        verify(button, never()).setOnClickListener(any())
    }

    @Test
    fun `Given button with click listener, when button model changed, then click listener should not be modified`() {
        val listener: View.OnClickListener = mock()
        button.setOnClickListener(listener)

        controller.model = controller.model.copy()

        verify(button, never()).setOnClickListener(argThat { this != listener })
    }
    //endregion
}