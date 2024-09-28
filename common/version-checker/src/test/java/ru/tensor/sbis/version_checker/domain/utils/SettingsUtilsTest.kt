package ru.tensor.sbis.version_checker.domain.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.PLAY_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_CRITICAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_RECOMMENDED

internal class SettingsUtilsTest {

    @Test
    fun `Checking behaviour bitmask contains sbis flags`() {
        val holder = getHolder(SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL)
        assertTrue(holder.useSbisRecommended())
        assertTrue(holder.useSbisCritical())
        assertFalse(holder.usePlayServiceRecommended())
    }

    @Test
    fun `Checking behaviour bitmask contains three flags`() {
        val holder = getHolder(SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL or PLAY_SERVICE_RECOMMENDED)
        assertTrue(holder.useSbisRecommended())
        assertTrue(holder.useSbisCritical())
        assertTrue(holder.usePlayServiceRecommended())
    }

    @Test
    fun `Checking behaviour bitmask contains mixed flags 1`() {
        val holder = getHolder(SBIS_SERVICE_RECOMMENDED or PLAY_SERVICE_RECOMMENDED)
        assertTrue(holder.useSbisRecommended())
        assertFalse(holder.useSbisCritical())
        assertTrue(holder.usePlayServiceRecommended())
    }

    private fun getHolder(flag: Int): VersioningSettings =
        object : VersioningSettings {
            override val appVersion = ""
            override val appId = ""
            override val appName = ""
            override fun getAppUpdateBehavior() = flag
        }
}