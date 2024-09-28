package ru.tensor.sbis.whats_new

import android.content.SharedPreferences
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class WhatsNewTest {

    @Mock
    private lateinit var mockSharedPrefs: SharedPreferences

    @Test
    fun `When app version has not changed, then don't show 'whats new' screen`() {
        val currentVersion = "22.1204"
        whenever(mockSharedPrefs.getString(any(), any())).doReturn(currentVersion)
        val whatsNewPreferences = createWhatsNewPreferenceManager(currentVersion)
        assert(whatsNewPreferences.restoreEntrance())
    }

    @Test
    fun `When app version has changed, then show 'whats new' screen`() {
        val currentVersion = "22.1204"
        val oldVersion = "21.6262"
        whenever(mockSharedPrefs.getString(any(), any())).doReturn(oldVersion)
        val whatsNewPreferences = createWhatsNewPreferenceManager(currentVersion)
        assert(whatsNewPreferences.restoreEntrance().not())
    }

    @Test
    fun `When app version has changed, but it bugfix, then don't show 'whats new' screen`() {
        val currentVersion = "22.1204.1"
        val oldVersion = "22.1204"
        whenever(mockSharedPrefs.getString(any(), any())).doReturn(oldVersion)
        val whatsNewPreferences = createWhatsNewPreferenceManager(currentVersion)
        assert(whatsNewPreferences.restoreEntrance())
    }

    @Test
    fun `When text for 'whats new' is empty, then don't show 'whats new' screen`() {
        val currentVersion = "22.1204"
        val oldVersion = "22.0102"
        val editor: SharedPreferences.Editor = mock()
        whenever(mockSharedPrefs.getString(any(), any())).doReturn(oldVersion)
        whenever(mockSharedPrefs.edit()).doReturn(editor)
        whenever(editor.putString(any(), any())).doReturn(editor)
        val whatsNewPreferences = createWhatsNewPreferenceManager(currentVersion, "")
        assert(whatsNewPreferences.restoreEntrance())
    }

    private fun createWhatsNewPreferenceManager(versionApp: String, whatsNewText: String = "test") =
        WhatsNewOnboardingPreferenceManagerImpl(mockSharedPrefs, versionApp, whatsNewText)
}