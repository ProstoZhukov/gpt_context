package ru.tensor.sbis.version_checker.domain.source

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory.Companion.UPDATE_SOURCE_KEY
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateSource
import ru.tensor.sbis.webviewer.contract.WebViewerFeature

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class UpdateCommandFactoryTest {

    private lateinit var updateCommandFactory: UpdateCommandFactory
    private var webViewerFeatureProvider: WebViewerFeature.Provider? = null

    @Before
    fun setUp() {
        webViewerFeatureProvider = buildWebViewerFeatureProvider()
    }

    @Test
    fun `All intents were correctly built and have extras for analytics`() {
        val updateSources = UpdateSource.values().toList()
        buildUpdateCommandFactory(updateSources)

        updateCommandFactory.create(true) { updateCommand, hasGooglePlay ->
            val intents = getIntents(updateCommand)

            assertEquals(updateSources.size, intents.size)
            intents.forEachIndexed { index, it ->
                val analytics = it.getStringExtra(UPDATE_SOURCE_KEY)
                assertNotNull(analytics)
                assertEquals(analytics, updateSources[index].toString())
                assertEquals(it.flags, Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            assertTrue(hasGooglePlay)
        }
    }

    @Test
    fun `If there is no google play update source, return false for analytics`() {
        val updateSources = UpdateSource.values().toMutableList()
        updateSources.remove(UpdateSource.GOOGLE_PLAY_STORE)
        buildUpdateCommandFactory(updateSources)

        updateCommandFactory.create { _, hasGooglePlay ->
            assertFalse(hasGooglePlay)
        }
    }

    @Test
    fun `If there is no WebViewerFeature, then build intent for browser`() {
        webViewerFeatureProvider = null
        buildUpdateCommandFactory(listOf(UpdateSource.SBIS_ONLINE))

        updateCommandFactory.create { updateCommand, _ ->
            val intents = getIntents(updateCommand)
            assertEquals(1, intents.size)
            assertEquals(Intent.ACTION_VIEW, intents[0].action)
        }
    }

    @Test
    fun `If there is WebViewerFeature, then build intent for webview`() {
        buildUpdateCommandFactory(listOf(UpdateSource.SBIS_ONLINE))

        updateCommandFactory.create { updateCommand, _ ->
            val intents = getIntents(updateCommand)
            assertEquals(1, intents.size)
            assertEquals("test_action", intents[0].action)
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun buildUpdateCommandFactory(updateSources: List<UpdateSource> = listOf()) {
        val mockPackageManager = mock<PackageManager> {
            on { getLaunchIntentForPackage(eq("ru.tensor.sbis.appmarket")) } doReturn Intent()
            on { getLaunchIntentForPackage(eq("org.ru.armax_tps570")) } doReturn Intent()
            on { getLaunchIntentForPackage(eq("com.newland.opennl.appstore")) } doReturn Intent()
        }
        val context = mock<Application> {
            on { packageManager } doReturn mockPackageManager
        }
        val mockDependency = mock<VersioningDependency> {
            on { webViewerFeatureProvider } doAnswer { webViewerFeatureProvider }
            on { getVersioningSettings() } doAnswer { getSettings(updateSources = updateSources) }
        }
        val mockDetector = mock<UpdateSourceDetector> {
            on { locateAll(any()) } doReturn updateSources
        }
        val settings = VersioningSettingsHolder(mockDependency)
        updateCommandFactory = UpdateCommandFactory(context, settings, mockDetector, mockDependency)
    }

    private fun buildWebViewerFeatureProvider() =
        object : WebViewerFeature.Provider {
            override val webViewerFeature: WebViewerFeature
                get() = object : WebViewerFeature {
                    override fun getDocumentViewerActivityIntent(
                        context: Context,
                        title: String?,
                        url: String,
                        uuid: String?
                    ): Intent = Intent("test_action")

                    override fun getDocumentViewerActivityIntentNoToolbar(context: Context, url: String): Intent =
                        Intent("test_action")
                }
        }

    @Suppress("UNCHECKED_CAST")
    private fun getIntents(updateCommand: UpdateCommand): List<Intent> {
        val field = UpdateCommand::class.java.getDeclaredField("intents")
        field.isAccessible = true
        return field.get(updateCommand) as List<Intent>
    }
}
