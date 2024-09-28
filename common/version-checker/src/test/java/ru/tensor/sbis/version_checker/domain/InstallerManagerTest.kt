package ru.tensor.sbis.version_checker.domain

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import org.mockito.kotlin.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.data.QrCodeLinkConverter
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory

internal class InstallerManagerTest {

    private val mockUri = mock<Uri> {
        on { toString() } doAnswer { testData }
    }
    private val mockIntent = mock<Intent> {
        on { data } doAnswer { testUri }
        on { flags } doAnswer { testFlags }
    }
    private val mockAppContext = mock<Context> {
        on { packageName } doAnswer { testAppIdInContext }
    }
    private val mockPackageManager = mock<PackageManager> {
        on { getLaunchIntentForPackage(any()) } doAnswer { testLaunchIntent }
    }
    private val mockContext = mock<Context> {
        on { applicationContext } doReturn mockAppContext
        on { packageManager } doReturn mockPackageManager
    }
    private val mockConverter = mock<QrCodeLinkConverter> {
        on { parse(anyOrNull()) } doAnswer { testAppIdInData }
    }
    private val mockCommand = mock<UpdateCommand> {
        on { run(anyOrNull()) } doAnswer { testOpenedMarket }
    }
    private val mockCommandFactory = mock<UpdateCommandFactory> {
        on { create(cleanAppId = any()) } doReturn mockCommand
    }
    private val mockAnalytics = mock<Analytics>()

    private var testUri: Uri? = mockUri
    private var testData: String? = null
    private var testAppIdInData: String? = "stranger_id"
    private var testAppIdInContext: String? = "app_id"
    private var testFlags: Int = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
    private var testLaunchIntent: Intent? = null
    private var testOpenedMarket: String? = null
    private lateinit var installerManager: InstallerManager

    @Before
    fun setUp() {
        installerManager = InstallerManager(
            qrCodeLinkMapper = mockConverter,
            updateFactory = mockCommandFactory,
            analytics = mockAnalytics
        )
    }

    @After
    fun tearDown() {
        testUri = mockUri
        testData = null
        testAppIdInData = "stranger_id"
        testAppIdInContext = "app_id"
        testFlags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        testLaunchIntent = null
        testOpenedMarket = null
    }

    @Test
    fun `Given intent with empty data, then immediately return false`() {
        testUri = null
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockIntent, atLeastOnce()).data
        assertFalse(result)
    }

    @Test
    fun `Given empty app id, then return false`() {
        testData = "qr_link"
        testAppIdInData = null
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockConverter).parse(testData!!)
        assertFalse(result)
    }

    @Test
    fun `Given app id equals current app, then return false`() {
        testAppIdInData = "app_id"
        testAppIdInContext = "app_id"

        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        assertFalse(result)
    }

    @Test
    fun `Given app id not equals current app and installed, then build intent and launch it`() {
        testLaunchIntent = mock()
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockPackageManager).getLaunchIntentForPackage(eq("stranger_id"))
        verify(testLaunchIntent)!!.data = eq(mockIntent.data)
        verify(mockContext).startActivity(testLaunchIntent)
        verify(mockAnalytics).send(AnalyticsEvent.GoInstalledApp())
        assertTrue(result)
    }

    @Test
    fun `Given app id not equals current app and NOT installed, then build intents for markets`() {
        installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockCommandFactory).create(eq("stranger_id"))
        verify(mockCommand).run(any())
    }

    @Test
    fun `Given debug id of NOT installed app, then build  market intent for clean id`() {
        testAppIdInData = "ru.tensor.sbis.business.debug"
        installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockCommandFactory).create(eq("ru.tensor.sbis.business"))
        verify(mockCommand).run(any())
    }

    @Test
    fun `Given intent with FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY flag, then immediately return false`() {
        testAppIdInData = "ru.tensor.sbis.business.debug"
        testFlags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        assertFalse(result)
        verify(mockCommandFactory, never()).create(eq("ru.tensor.sbis.business"))
        verify(mockCommand, never()).run(any())
    }

    @Test
    fun `Given intent with FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY flag, then clear intent data`() {
        testAppIdInData = "ru.tensor.sbis.business.debug"
        testFlags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT

        installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockIntent).setData(eq(null))
    }

    @Test
    fun `When open market to install app, then back true and send analytics`() {
        testOpenedMarket = "google_play"
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockAnalytics).send(AnalyticsEvent.GoInstallApp())
        assertTrue(result)
    }

    @Test
    fun `When not open market to install app, then back false and not send analytics`() {
        val result = installerManager.handleInstallationCase(mockContext, mockIntent)

        verify(mockAnalytics, never()).send(AnalyticsEvent.GoInstallApp())
        assertFalse(result)
    }
}