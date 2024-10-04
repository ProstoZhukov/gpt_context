package ru.tensor.sbis.design.confirmation_dialog

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils

/**
 * Тест верного выстраивания кнопок в зависимости от ориентации контейнера.
 * @author ma.kolpakov
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ConfirmationDialogTests(
    private val orientation: ConfirmationButtonOrientation,
    private val buttonsCount: Int,
    private val isButtonFitContainerHorizontal: Boolean,
    private val isButtonFitContainerVertical: Boolean
) {
    lateinit var context: Context

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        context = activity
    }

    @Test
    fun `Buttons is fit to container depends on orientation`() {
        val containerSize = 100
        val buttonWith = 40
        val buttonHeight = 20
        val buttonContainer = ButtonContainer(context)
        buttonContainer.orientation = orientation
        var lastButton: View? = null
        repeat(buttonsCount) {
            lastButton = View(context).apply {
                buttonContainer.addView(this)
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = buttonWith
                    height = buttonHeight
                }
            }
        }

        buttonContainer.measure(
            MeasureSpecUtils.makeExactlySpec(containerSize),
            MeasureSpecUtils.makeExactlySpec(containerSize)
        )
        buttonContainer.layout(0, 0, containerSize, containerSize)
        assertEquals(isButtonFitContainerHorizontal, lastButton!!.right < containerSize)
        assertEquals(isButtonFitContainerVertical, lastButton!!.bottom < containerSize)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun testParams() = params {
            add(ConfirmationButtonOrientation.HORIZONTAL, 2, true, true)
            add(ConfirmationButtonOrientation.HORIZONTAL, 3, false, true)

            add(ConfirmationButtonOrientation.VERTICAL, 2, true, true)
            add(ConfirmationButtonOrientation.VERTICAL, 4, true, false)

            add(ConfirmationButtonOrientation.AUTO, 2, true, true)
            add(ConfirmationButtonOrientation.AUTO, 3, true, true)
            add(ConfirmationButtonOrientation.AUTO, 4, true, false)
        }
    }
}
