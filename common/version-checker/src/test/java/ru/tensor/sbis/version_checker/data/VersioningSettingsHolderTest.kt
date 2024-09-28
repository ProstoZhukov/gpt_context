package ru.tensor.sbis.version_checker.data

import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

@RunWith(JUnitParamsRunner::class)
internal class VersioningSettingsHolderTest {

    private companion object {
        private const val CRITICAL_VERSION = "22.1218"
        private const val RECOMMENDED_VERSION = "22.4125"
    }

    private lateinit var settingsHolder: VersioningSettingsHolder
    private val remoteResult = RemoteVersioningSettingResult(
        critical = Version(CRITICAL_VERSION),
        recommended = Version(RECOMMENDED_VERSION)
    )

    @Test
    fun `Verify clean app id without debug prefix`() {
        buildSettingsHolder("ru.tensor.sbis.business.debug")
        assertEquals("ru.tensor.sbis.business", settingsHolder.cleanAppId)
    }

    @Test
    fun `Update remote settings`() {
        buildSettingsHolder()
        assertTrue(settingsHolder.remote.isEmpty)

        settingsHolder.update(remoteResult)
        assertEquals(settingsHolder.remote, remoteResult)
    }

    @Test
    @Parameters(method = "getUpdateStatusWithVersion")
    fun `Get correct version depending on update status`(updateStatus: UpdateStatus, version: String?) {
        buildSettingsHolder()
        settingsHolder.update(remoteResult)
        val result = settingsHolder.remoteVersionFor(updateStatus)
        assertEquals(version, result?.version)
    }

    private fun buildSettingsHolder(appId: String = "ru.tensor.sbis.business") {
        val dependency = mock<VersioningDependency> {
            on { getVersioningSettings() } doAnswer { getSettings(appId) }
        }
        settingsHolder = VersioningSettingsHolder(dependency)
    }

    @Suppress("unused")
    private fun getUpdateStatusWithVersion() = params {
        add(UpdateStatus.Mandatory, CRITICAL_VERSION)
        add(UpdateStatus.Recommended, RECOMMENDED_VERSION)
        add(UpdateStatus.Empty, null)
    }
}
