package ru.tensor.sbis.design.navigation.view.model

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author us.bessonov
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class NavigationPreferencesTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `When item states are saved successively, then restored values are as expected`() {
        val preferences = NavigationPreferences(context)

        preferences.saveState("first", false)
        preferences.saveState("second", false)
        preferences.saveState("first", true)
        preferences.saveState("third", false)

        assertTrue(preferences.isExpanded("first"))
        assertFalse(preferences.isExpanded("second"))
        assertFalse(preferences.isExpanded("third"))
    }

}