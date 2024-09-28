package ru.tensor.sbis.version_checker.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class RemoteVersioningSettingResultTest {

    @Test
    fun `Empty() instance equals isEmpty`() {
        assertTrue(RemoteVersioningSettingResult.empty().isEmpty)
    }

    @Test
    fun `Not empty instance is not isEmpty`() {
        val version = Version("1.0")
        assertFalse(RemoteVersioningSettingResult(version, null).isEmpty)
        assertFalse(RemoteVersioningSettingResult(null, version).isEmpty)
    }
}
