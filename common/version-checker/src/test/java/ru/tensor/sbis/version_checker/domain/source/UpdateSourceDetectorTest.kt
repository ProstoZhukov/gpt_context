package ru.tensor.sbis.version_checker.domain.source

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import org.mockito.kotlin.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.RemoteVersioningSettingResult
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateSource
import java.lang.reflect.Field
import java.lang.reflect.Modifier

@Suppress("JUnitMalformedDeclaration")
@RunWith(JUnitParamsRunner::class)
internal class UpdateSourceDetectorTest {

    private var allPackagesSupported = true
    private var mockPackageManager: PackageManager = mock {
        @Suppress("DEPRECATION")
        on { getPackageInfo(anyString(), eq(0)) } doAnswer {
            if (allPackagesSupported) PackageInfo() else throw PackageManager.NameNotFoundException()
        }
        on { getPackageInfo(anyString(), any<PackageManager.PackageInfoFlags>()) } doAnswer {
            if (allPackagesSupported) PackageInfo() else throw PackageManager.NameNotFoundException()
        }
    }
    private var mockApplication = mock<Application> {
        on { packageName } doReturn "com.test.example"
        on { packageManager } doReturn mockPackageManager
    }
    private lateinit var detector: UpdateSourceDetector
    private lateinit var settings: VersioningSettingsHolder

    @After
    fun tearDown() {
        allPackagesSupported = true
    }

    @Test
    fun `Get updateSource from settingsHolder if any`() {
        buildDetector()
        settings.update(RemoteVersioningSettingResult(null, null))
        assertEquals(UpdateSource.SBIS_MARKET, detector.locateAll().first())
    }

    @Test
    @Parameters("28", "31")
    fun `Try to get updateSource from installer source`(testSdkInt: Int) {
        buildDetector()
        setBuildVersionSdkInt(testSdkInt)

        detector.locateAll()
        if (testSdkInt >= 30) {
            verify(mockPackageManager).getInstallSourceInfo(mockApplication.packageName)
        } else {
            @Suppress("DEPRECATION")
            verify(mockPackageManager).getInstallerPackageName(mockApplication.packageName)
        }
    }

    @Test
    fun `If supportedSources are not installed, do not use them`() {
        buildDetector(UpdateSource.values().toList())
        allPackagesSupported = false
        val locatedSources = detector.locateAll()
        assertEquals(UpdateSource.SBIS_ONLINE, locatedSources.first())
        assertEquals(1, locatedSources.size)
    }

    @Test
    fun `Default updateSource is sbisMarket, googlePlay, sbisOnline`() {
        buildDetector()
        val locatedSources = detector.locateAll()
        assertEquals(3, locatedSources.size)
        assertEquals(UpdateSource.SBIS_MARKET, locatedSources[0])
        assertEquals(UpdateSource.GOOGLE_PLAY_STORE, locatedSources[1])
        assertEquals(UpdateSource.SBIS_ONLINE, locatedSources[2])
    }

    @Test
    fun `If there are no supported updateSources, use sbisOnline`() {
        buildDetector(emptyList())
        val locatedSources = detector.locateAll()
        assertEquals(1, locatedSources.size)
        assertEquals(UpdateSource.SBIS_ONLINE, detector.locateAll().last())
    }

    @Test
    fun `All updateSources are used in detector`() {
        val allUpdateSources = UpdateSource.values().toList()
        buildDetector(allUpdateSources)
        val locatedSources = detector.locateAll()
        assertEquals(locatedSources.size, allUpdateSources.size)
    }

    private fun buildDetector(supportedSources: List<UpdateSource>? = null) {
        val dependency = mock<VersioningDependency> {
            on { getVersioningSettings() } doAnswer { getSettings(updateSources = supportedSources) }
        }
        settings = VersioningSettingsHolder(dependency)
        detector = UpdateSourceDetector(mockApplication, settings)
    }

    private fun setBuildVersionSdkInt(value: Any) {
        val field = Build.VERSION::class.java.getDeclaredField("SDK_INT")
        field.isAccessible = true
        getModifiersField().also {
            it.isAccessible = true
            it.set(field, field.modifiers and Modifier.FINAL.inv())
        }
        field.set(null, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getModifiersField(): Field {
        return try {
            Field::class.java.getDeclaredField("modifiers")
        } catch (e: NoSuchFieldException) {
            try {
                val getDeclaredFields0 =
                    Class::class.java.getDeclaredMethod(
                        "getDeclaredFields0",
                        Boolean::class.javaPrimitiveType
                    )
                getDeclaredFields0.isAccessible = true
                val fields = getDeclaredFields0.invoke(Field::class.java, false) as Array<Field>
                for (field in fields) {
                    if ("modifiers" == field.name) {
                        return field
                    }
                }
            } catch (ex: ReflectiveOperationException) {
                e.addSuppressed(ex)
            }
            throw e
        }
    }
}
