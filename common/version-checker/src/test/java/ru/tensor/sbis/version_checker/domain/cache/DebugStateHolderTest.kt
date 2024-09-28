package ru.tensor.sbis.version_checker.domain.cache

import android.content.SharedPreferences
import org.mockito.kotlin.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

@RunWith(JUnitParamsRunner::class)
internal class DebugStateHolderTest {

    private companion object {
        private val LAST_APP_VERSION = VersioningLocalCache::class.java.canonicalName!! + ".last_app_version"
        private const val VERSION_LOCAL_DEBUG_KEY = "VERSION_LOCAL_DEBUG_KEY"
        private const val DEBUG_UPDATE_STATUS_KEY = "DEBUG_UPDATE_TYPE_KEY"
        private const val RESET_LOCAL_DEBUG_KEY = "RESET_LOCAL_DEBUG_KEY"
    }

    private lateinit var debugStateHolder: DebugStateHolder
    private lateinit var settingsHolder: VersioningSettingsHolder
    private lateinit var mockPreferences: SharedPreferences
    private lateinit var mockPreferencesEditor: SharedPreferences.Editor
    private var localDebugKey: String? = null
    private var lastAppVersion: String? = null
    private var updateStatusId: Int? = null
    private var resetLocalDebugKey: Boolean = false

    @After
    fun tearDown() {
        lastAppVersion = null
        updateStatusId = null
        localDebugKey = null
        resetLocalDebugKey = false
    }

    @Test
    fun `Update app version on init`() {
        buildDebugStateHolder()
        verify(mockPreferencesEditor).putString(LAST_APP_VERSION, settingsHolder.appVersion)
    }

    @Test
    fun `Update app version on init and remove debug version on real version change`() {
        lastAppVersion = "1.0"
        buildDebugStateHolder(appVersion = "0.9")
        verify(mockPreferencesEditor).remove(VERSION_LOCAL_DEBUG_KEY)
        verify(mockPreferencesEditor).putString(LAST_APP_VERSION, settingsHolder.appVersion)
    }

    @Test
    fun `Do not update last app version if it's not changed`() {
        lastAppVersion = "1.0"
        buildDebugStateHolder(appVersion = "1.0")
        verify(mockPreferencesEditor, never()).remove(VERSION_LOCAL_DEBUG_KEY)
        verify(mockPreferencesEditor, never()).putString(LAST_APP_VERSION, settingsHolder.appVersion)
    }

    @Test
    @Parameters(value = ["0", "1", "2"])
    fun `On getUpdateDebugStatus() return correct status from preferences`(prefUpdateStatusId: Int?) {
        buildDebugStateHolder()
        updateStatusId = prefUpdateStatusId
        val result = debugStateHolder.getUpdateDebugStatus()
        assertEquals(result.id, prefUpdateStatusId)
    }

    @Test
    fun `On getDebugVersion() return lastDebugMandatoryVersion if any`() {
        buildDebugStateHolder()
        setLastDebugMandatoryVersion()
        localDebugKey = "0.2"
        assertEquals("0.1", debugStateHolder.getDebugVersion().version)
    }

    @Test
    fun `On setDebugVersion() save version to preferences with right key`() {
        buildDebugStateHolder()
        debugStateHolder.setDebugVersion("10.0")
        verify(mockPreferencesEditor).putString(eq(VERSION_LOCAL_DEBUG_KEY), eq("10.0"))
    }

    @Test
    fun `On setUpdateDebugStatus() save status to preferences with right key`() {
        buildDebugStateHolder()
        debugStateHolder.setUpdateDebugStatus(UpdateStatus.Mandatory)
        verify(mockPreferencesEditor).putInt(eq(DEBUG_UPDATE_STATUS_KEY), eq(2))
    }

    @Test
    @Parameters(value = ["0", "1"])
    fun `Do not reset debug lock if update status is not mandatory`(newUpdateStatusId: Int) {
        buildDebugStateHolder()
        updateStatusId = newUpdateStatusId
        verify(mockPreferences, never()).getBoolean(eq(RESET_LOCAL_DEBUG_KEY), any())
        verify(mockPreferencesEditor, never()).putBoolean(eq(RESET_LOCAL_DEBUG_KEY), any())
    }

    @Test
    fun `Do not reset debug lock if preferences don't contain debug version`() {
        updateStatusId = 2
        buildDebugStateHolder()
        verify(mockPreferences, never()).getBoolean(eq(RESET_LOCAL_DEBUG_KEY), any())
        verify(mockPreferencesEditor, never()).putBoolean(eq(RESET_LOCAL_DEBUG_KEY), any())
    }

    @Test
    fun `Delay reset debug lock if matched all conditions`() {
        updateStatusId = 2
        localDebugKey = "0.5"
        buildDebugStateHolder()
        debugStateHolder.resetDebugLock()
        verify(mockPreferences).getBoolean(eq(RESET_LOCAL_DEBUG_KEY), eq(false))
        verify(mockPreferencesEditor).putBoolean(eq(RESET_LOCAL_DEBUG_KEY), eq(true))
        verify(mockPreferencesEditor, never()).remove(eq(VERSION_LOCAL_DEBUG_KEY))
        verify(mockPreferencesEditor, never()).remove(eq(RESET_LOCAL_DEBUG_KEY))
    }

    @Test
    fun `Reset debug lock key if matched all conditions`() {
        updateStatusId = 2
        localDebugKey = "0.5"
        resetLocalDebugKey = true
        buildDebugStateHolder()
        debugStateHolder.resetDebugLock()
        verify(mockPreferences).getBoolean(eq(RESET_LOCAL_DEBUG_KEY), eq(false))
        verify(mockPreferencesEditor).remove(eq(VERSION_LOCAL_DEBUG_KEY))
        verify(mockPreferencesEditor).remove(eq(RESET_LOCAL_DEBUG_KEY))
    }

    private fun buildDebugStateHolder(appVersion: String = "1.0") {
        mockPreferencesEditor = mock {
            on { putString(anyString(), anyString()) } doReturn mock()
            on { putInt(anyString(), anyInt()) } doReturn mock()
            on { remove(anyString()) } doReturn mock()
        }
        doNothing().whenever(mockPreferencesEditor).apply()
        mockPreferences = mock {
            on { edit() } doReturn mockPreferencesEditor
            on { contains(eq(LAST_APP_VERSION)) } doAnswer { lastAppVersion != null }
            on { contains(eq(VERSION_LOCAL_DEBUG_KEY)) } doAnswer { localDebugKey != null }
            on { getBoolean(eq(RESET_LOCAL_DEBUG_KEY), any()) } doAnswer { resetLocalDebugKey }
            on { getString(eq(LAST_APP_VERSION), anyString()) } doAnswer { lastAppVersion }
            on { getString(eq(VERSION_LOCAL_DEBUG_KEY), any()) } doAnswer { localDebugKey }
            on { getInt(eq(DEBUG_UPDATE_STATUS_KEY), any()) } doAnswer { updateStatusId }
        }
        val mockDependency = mock<VersioningDependency> {
            on { getVersioningSettings() } doAnswer { getSettings(appVersion = appVersion) }
        }
        settingsHolder = VersioningSettingsHolder(mockDependency)
        debugStateHolder = DebugStateHolder(mockPreferences, settingsHolder)
    }

    private fun setLastDebugMandatoryVersion() {
        updateStatusId = 2 // Mandatory
        localDebugKey = "0.1"
        resetLocalDebugKey = true
        debugStateHolder.resetDebugLock() // Установка lastDebugMandatoryVersion == localDebugKey
    }
}