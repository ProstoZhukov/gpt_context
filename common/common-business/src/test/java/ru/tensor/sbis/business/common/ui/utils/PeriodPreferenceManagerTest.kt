package ru.tensor.sbis.business.common.ui.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringStartsWith.startsWith
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.business.common.ui.prefs.PeriodPreferenceManager
import ru.tensor.sbis.business.common.ui.utils.period.CurrentAndPastPeriod
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class PeriodPreferenceManagerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val periodManager = PeriodPreferenceManager(prefs, TEST_PREF_PREFIX)

    private val testPeriod = DatePeriod(
        from = Date().set(2019, Calendar.MAY, 1), to = Date().set(2019, Calendar.MAY, 31)
    )
    private val testPastPeriod = DatePeriod(
        from = Date().set(2018, Calendar.MAY, 1), to = Date().set(2018, Calendar.MAY, 31)
    )
    private val testComparedPeriods = CurrentAndPastPeriod(
        current = testPeriod, past = testPastPeriod
    )
    private val testDefaultPeriod = DatePeriod(
        from = Date().set(2017, Calendar.AUGUST, 0), to = Date().set(2017, Calendar.AUGUST, 30)
    )

    @After
    fun tearDown() {
        prefs.edit().clear().apply()
    }

    @Test
    fun `All keys start of optional prefix`() {
        assertThat(periodManager.prefPeriodFrom, startsWith(TEST_PREF_PREFIX))
        assertThat(periodManager.prefPeriodTo, startsWith(TEST_PREF_PREFIX))
        assertThat(periodManager.prefPastPeriodFrom, startsWith(TEST_PREF_PREFIX))
        assertThat(periodManager.prefPastPeriodTo, startsWith(TEST_PREF_PREFIX))
    }

    @Test
    fun `Restore correct PERIOD value after save`() {
        periodManager.savePeriod(testPeriod)

        assertEquals(testPeriod, periodManager.restorePeriod())
    }

    @Test
    fun `Restore correct PERIODS value after save`() {
        periodManager.savePeriods(testComparedPeriods)

        assertEquals(testComparedPeriods, periodManager.restorePeriods())
    }


    @Test
    fun `Restore default PERIOD value if there was NOT save`() {
        val restoredPeriod = periodManager.restorePeriod { testPeriod }
        assertEquals(testPeriod, restoredPeriod)
    }

    @Test
    fun `Restore default PERIODS value if there was NOT save`() {
        val restoredPeriods = periodManager.restorePeriods { testPeriod }
        assertEquals(testPeriod, restoredPeriods.current)
    }

    @Test
    fun `Ignore default PERIOD value if there was save`() {
        periodManager.savePeriod(testPeriod)
        val restoredPeriod = periodManager.restorePeriod { testDefaultPeriod }
        assertEquals(testPeriod, restoredPeriod)
    }

    @Test
    fun `Restore default PERIOD value if saved broken period`() {
        periodManager.savePeriod(testPeriod)
        prefs.edit().remove(periodManager.prefPeriodTo).apply()

        val restoredPeriod = periodManager.restorePeriod { testDefaultPeriod }
        assertEquals(restoredPeriod, testDefaultPeriod)
    }

    @Test
    fun `Restore default PERIODS value if saved broken periods`() {
        periodManager.savePeriods(testComparedPeriods)
        prefs.edit().remove(periodManager.prefPastPeriodTo).apply()

        val restoredPeriod = periodManager.restorePeriods { testDefaultPeriod }
        assertEquals(restoredPeriod.current, testDefaultPeriod)
    }

    @Test
    fun `Replace stored periods using any method`() {
        periodManager.savePeriod(testPastPeriod)
        periodManager.savePeriods(testComparedPeriods)

        val restoredPeriod = periodManager.restorePeriod { testDefaultPeriod }
        assertEquals(restoredPeriod, testPeriod)
    }

    @Test
    fun `Shared preferences contain keys after save`() {
        periodManager.savePeriods(testComparedPeriods)

        assertTrue(prefs.contains(periodManager.prefPeriodFrom))
        assertTrue(prefs.contains(periodManager.prefPeriodTo))
        assertTrue(prefs.contains(periodManager.prefPastPeriodFrom))
        assertTrue(prefs.contains(periodManager.prefPastPeriodTo))
    }
}

private const val SHARED_PREFERENCES_NAME = "test_prefs"
private const val TEST_PREF_PREFIX = "test_prefix"
