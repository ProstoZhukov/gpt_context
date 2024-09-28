package ru.tensor.sbis.version_checker.testUtils

import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import org.robolectric.annotation.Implements
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.di.singleton.VersioningSingletonComponent
import ru.tensor.sbis.version_checker.di.subcomponents.DebugUpdateFragmentComponent
import ru.tensor.sbis.version_checker.di.subcomponents.RecommendedUpdateFragmentComponent
import ru.tensor.sbis.version_checker.di.subcomponents.RequiredUpdateFragmentComponent
import ru.tensor.sbis.version_checker.domain.InstallerManager
import ru.tensor.sbis.version_checker.domain.VersionManager
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory
import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.PLAY_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_CRITICAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.UpdateSource
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

internal const val TEST_APP_ID = "ru.tensor.sbis.business"
internal const val TEST_CRITICAL_VERSION = "22.1218"
internal const val TEST_RECOMMENDED_VERSION = "22.4125"

internal fun getSettings(
    appVersion: String = "1.0",
    appId: String? = TEST_APP_ID,
    updateSources: List<UpdateSource>? = null,
    appUpdateBehavior: Int = SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL or PLAY_SERVICE_RECOMMENDED
) = if (updateSources != null) {
    object : VersioningSettings {
        override val appVersion: String = appVersion
        override val appId: String = appId ?: ""
        override val appName: String = "Business"
        override fun getAppUpdateBehavior(): Int = appUpdateBehavior
        override fun getUpdateSource(): List<UpdateSource> = updateSources
    }
} else {
    object : VersioningSettings {
        override val appVersion: String = appVersion
        override val appId: String = appId ?: ""
        override val appName: String = "Business"
        override fun getAppUpdateBehavior(): Int = appUpdateBehavior
    }
}

@Suppress("SpellCheckingInspection")
internal fun getAndroidVersionsJson(
    appId: String = TEST_APP_ID,
    usePublishedVersion: Boolean = true
): JSONObject {
    val jsonObject = JSONObject()

    val versionsJson = JSONObject()
    versionsJson.put("ru.tensor.sbis.droid.saby", "21.3119")
    versionsJson.put(appId, TEST_CRITICAL_VERSION)

    val publishedVersionsJson = JSONObject()
    publishedVersionsJson.put("ru.tensor.sbis.droid.saby", "21.3119")
    if (usePublishedVersion) {
        publishedVersionsJson.put(appId, TEST_RECOMMENDED_VERSION)
    }

    jsonObject.put("versions", versionsJson)
    jsonObject.put("published_versions", publishedVersionsJson)
    return jsonObject
}

@Suppress("unused")
@Implements(VersionCheckerPlugin::class)
internal class ShadowVersionCheckerPlugin {
    companion object {
        internal lateinit var shadowState: MutableStateFlow<UpdateStatus>
        internal var shadowVersionManager: VersionManager = mock {
            on { state } doAnswer { shadowState }
        }
        internal var mockInstallerManager: InstallerManager = mock()

        @JvmStatic
        internal val versioningComponent = object : VersioningSingletonComponent {
            override val versionManager: VersionManager = shadowVersionManager
            override val installerManager: InstallerManager = mockInstallerManager
            override val commandFactory: UpdateCommandFactory = mock()
            override val versionDependency: VersioningDependency = mock()
            override val analytics: Analytics = mock()
            override fun recommendedComponentFactory(): RecommendedUpdateFragmentComponent = mock()
            override fun requiredComponentFactory(): RequiredUpdateFragmentComponent = mock()
            override fun debugComponentFactory(): DebugUpdateFragmentComponent = mock()
        }
    }
}
