package ru.tensor.sbis.version_checker.data

import android.os.Build
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.testUtils.*
import ru.tensor.sbis.version_checker.testUtils.TEST_APP_ID
import ru.tensor.sbis.version_checker.testUtils.TEST_CRITICAL_VERSION
import ru.tensor.sbis.version_checker.testUtils.getAndroidVersionsJson
import ru.tensor.sbis.version_checker.testUtils.getSettings

@RunWith(RobolectricTestRunner::class) // для работы JsonObject()
@Config(sdk = [Build.VERSION_CODES.R])
internal class VersionMapperTest {

    private var currentAppId = TEST_APP_ID
    private lateinit var settings: VersioningSettingsHolder
    private lateinit var versionMapper: VersionMapper
    private var usePublishedVersion = true

    @Before
    fun setUp() {
        buildVersionMapper()
    }

    @After
    fun tearDown() {
        usePublishedVersion = true
        currentAppId = TEST_APP_ID
    }

    @Test
    fun `Parse critical, recommended version and updateSource`() {
        val result = versionMapper.apply(getJson())
        assertEquals(TEST_CRITICAL_VERSION, result.critical?.version)
        assertEquals(TEST_RECOMMENDED_VERSION, result.recommended?.version)
    }

    @Test
    fun `Return null if parse by appId is unsuccessful`() {
        currentAppId = "com.test.example"
        val result = versionMapper.apply(getJson())
        assertNull(result.critical)
        assertNull(result.recommended)
    }

    private fun buildVersionMapper() {
        val mockDependency: VersioningDependency = mock {
            on { getVersioningSettings() } doAnswer { getSettings(currentAppId) }
        }
        settings = VersioningSettingsHolder(mockDependency)
        versionMapper = VersionMapper(settings)
    }

    private fun getJson() = getAndroidVersionsJson(currentAppId, usePublishedVersion)
}