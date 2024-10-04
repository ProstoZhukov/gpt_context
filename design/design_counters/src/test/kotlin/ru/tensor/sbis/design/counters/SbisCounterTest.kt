package ru.tensor.sbis.design.counters

import android.app.Activity
import android.os.Build
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterDrawable
import ru.tensor.sbis.design.theme.res.SbisDimen

/**
 * Тесты класса [SbisCounterDrawable].
 *
 * @author da.zolotarev
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class SbisCounterTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

    private lateinit var counterDrawable: SbisCounterDrawable

    @Before
    fun setup() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        counterDrawable = SbisCounterDrawable(activity)
    }

    @Test
    fun `When custom border radius is set, then border radius in counter has the same value`() {
        counterDrawable.customBorderRadius = SbisDimen.Px(BORDER_RADIUS)
        assertEquals(BORDER_RADIUS, counterDrawable.customCornerRadiusActual?.toInt())
    }

    companion object {
        const val BORDER_RADIUS = 4
    }
}