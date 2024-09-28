package ru.tensor.sbis.version_checker.domain

import android.os.Looper
import android.os.MessageQueue
import androidx.fragment.app.FragmentManager
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.on
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.ChangeHostEvent
import ru.tensor.sbis.verification_decl.login.event.HostEvent
import ru.tensor.sbis.verification_decl.login.event.InitHostEvent
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.RemoteVersioningSettingResult
import ru.tensor.sbis.version_checker.data.Version
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.domain.cache.DebugStateHolder
import ru.tensor.sbis.version_checker.domain.cache.VersioningLocalCache
import ru.tensor.sbis.version_checker.domain.service.InAppUpdateChecker
import ru.tensor.sbis.version_checker.domain.service.VersionServiceChecker
import ru.tensor.sbis.version_checker.domain.utils.usePlayServiceRecommended
import ru.tensor.sbis.version_checker.domain.utils.useSbisCritical
import ru.tensor.sbis.version_checker.domain.utils.useSbisRecommended
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker.ui.recommended.RecommendedUpdateFragment
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.PLAY_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_CRITICAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

@Suppress("JUnitMalformedDeclaration")
@ExperimentalCoroutinesApi
@RunWith(JUnitParamsRunner::class)
internal class VersionManagerTest {

    private val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var versionManager: VersionManager
    private lateinit var settingsHolder: VersioningSettingsHolder
    private lateinit var debugStateHolder: DebugStateHolder
    private lateinit var mockSbisVersionChecker: VersionServiceChecker
    private lateinit var mockInAppUpdateChecker: InAppUpdateChecker
    private lateinit var mockLocalCache: VersioningLocalCache
    private lateinit var mockHostEventObservable: Subject<HostEvent>

    private val mockAnalytics: Analytics = mock()
    private var isDebugOn = false
    private var appVersion = "1.1"
    private var debugUpdateStatus: UpdateStatus = UpdateStatus.Empty
    private var criticalVersion = Version("0.5")
    private var recommendedVersion = Version("1.0")
    private var debugVersion = Version("0.1")
    private var isRemoteSettingsExpired = false
    private var isRecommendationExpired = false
    private var inAppUpdateResult = false
    private val sbisVersionCheckerResult = RemoteVersioningSettingResult(criticalVersion, recommendedVersion)
    private var testAppUpdateBehavior = SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL or PLAY_SERVICE_RECOMMENDED
    private var skipRecommendationIntervalFeature = false

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        isDebugOn = false
        debugUpdateStatus = UpdateStatus.Empty
        isRemoteSettingsExpired = false
        isRecommendationExpired = false
        inAppUpdateResult = false
        testAppUpdateBehavior = SBIS_SERVICE_RECOMMENDED or SBIS_SERVICE_CRITICAL or PLAY_SERVICE_RECOMMENDED
        skipRecommendationIntervalFeature = false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `On init manager start checking, if it wasn't init already`() {
        buildVersionManager()
        versionManager.init()
        verify(settingsHolder, atLeastOnce()).useSbisCritical()
        verify(settingsHolder, atLeastOnce()).useSbisRecommended()
        verify(settingsHolder, atLeastOnce()).usePlayServiceRecommended()
        clearInvocations(settingsHolder)

        versionManager.init()
        verify(settingsHolder, never()).useSbisCritical()
        verify(settingsHolder, never()).useSbisRecommended()
        verify(settingsHolder, never()).usePlayServiceRecommended()
    }

    @Test
    @Parameters(method = "variousUpdateStatus")
    fun `On check compatibility use debug version to check if debug mode is on`(updateStatus: UpdateStatus) {
        buildVersionManager()
        isDebugOn = true
        debugUpdateStatus = updateStatus

        versionManager.init()
        verify(debugStateHolder).getDebugVersion()
        assertEquals(updateStatus, versionManager.state.value)
    }

    @Test
    fun `On check compatibility if debug is on and debug status doesn't match then do nothing`() {
        buildVersionManager()
        isDebugOn = true
        debugUpdateStatus = UpdateStatus.Empty
        versionManager.init()

        assertEquals(UpdateStatus.Empty, versionManager.state.value)
        verify(settingsHolder, never()).remoteVersionFor(any())
        verify(debugStateHolder, never()).getDebugVersion()
    }

    @Test
    @Parameters(method = "variousUpdateStatus")
    fun `On check compatibility if debug is on if installed version is unspecified then do nothing`(
        updateStatus: UpdateStatus
    ) {
        buildVersionManager()
        isDebugOn = true
        debugUpdateStatus = updateStatus // 2
        debugVersion = Version("")
        versionManager.init()

        assertEquals(UpdateStatus.Empty, versionManager.state.value)
        verify(settingsHolder, never()).remoteVersionFor(any())
    }

    @Test
    @Parameters(method = "variousUpdateStatus")
    fun `On check compatibility if debug is on get correct version depending on status`(
        updateStatus: UpdateStatus
    ) {
        buildVersionManager()
        isDebugOn = true
        debugUpdateStatus = updateStatus
        versionManager.init()

        verify(settingsHolder).remoteVersionFor(argThat { this == debugUpdateStatus })
    }

    @Test
    fun `On mandatory update reset debug lock`() {
        buildVersionManager()
        isDebugOn = true
        debugUpdateStatus = UpdateStatus.Mandatory
        appVersion = "0.4"
        versionManager.init()

        verify(debugStateHolder).resetDebugLock()
        verify(settingsHolder).remoteVersionFor(argThat { this == debugUpdateStatus })
        assertEquals(UpdateStatus.Mandatory, versionManager.state.value)
    }

    @Test
    fun `Do not update if settings not expired`() {
        buildVersionManager()
        versionManager.init()
        mockHostEventObservable.onNext(InitHostEvent)

        verify(mockLocalCache).isRemoteVersionSettingsExpired()
        verify(mockSbisVersionChecker, never()).update()
    }

    @Test
    fun `On change host event start update without checking if settings expired`() {
        buildVersionManager()
        versionManager.init()
        mockHostEventObservable.onNext(ChangeHostEvent)

        verify(mockLocalCache, never()).isRemoteVersionSettingsExpired()
        verify(mockSbisVersionChecker).update()
    }

    @Test
    fun `Start update if expired, then save result and check compatibility`() {
        buildVersionManager()
        versionManager.init()
        clearInvocations(settingsHolder)
        isRemoteSettingsExpired = true
        mockHostEventObservable.onNext(InitHostEvent)

        verify(mockSbisVersionChecker).update()
        verify(settingsHolder).update(argThat { this == sbisVersionCheckerResult })
        verify(mockLocalCache).saveDictionary(argThat { this == sbisVersionCheckerResult })
        verify(settingsHolder, times(2)).remoteVersionFor(any())
    }

    @Test
    fun `If useSbisRecommended or useSbisCritical is not enabled, do not update remote settings`() {
        testAppUpdateBehavior = PLAY_SERVICE_RECOMMENDED
        buildVersionManager()
        versionManager.init()
        clearInvocations(settingsHolder)
        isRemoteSettingsExpired = true
        mockHostEventObservable.onNext(InitHostEvent)

        verify(mockSbisVersionChecker, never()).update()
        verify(settingsHolder, never()).update(any())
        verify(mockLocalCache, never()).saveDictionary(any())
    }

    @Test
    fun `Do not show recommended fragment if recommendation expired`() {
        buildVersionManager()
        val mockFragmentManager = mock<FragmentManager>()
        versionManager.showRecommendedFragment(mockFragmentManager)

        verify(mockLocalCache).isRecommendationExpired()
        verify(mockFragmentManager, never()).beginTransaction()
        verify(mockFragmentManager, never()).findFragmentByTag(anyOrNull())
    }

    @Test
    fun `Do not show recommended fragment if it is already shown`() {
        buildVersionManager()
        val mockFragmentManager = mock<FragmentManager> {
            on { findFragmentByTag(anyString()) } doReturn mock<RecommendedUpdateFragment>()
        }
        isRecommendationExpired = true
        versionManager.showRecommendedFragment(mockFragmentManager)

        verify(mockLocalCache).isRecommendationExpired()
        verify(mockFragmentManager).findFragmentByTag(anyOrNull())
        verify(mockFragmentManager, never()).beginTransaction()
    }

    @Test
    fun `Do not show recommended fragment if skip recommendation interval feature enabled expired it was already shown`() {
        val looperMock = mockLooper()
        buildVersionManager()

        skipRecommendationIntervalFeature = true
        isRecommendationExpired = true

        var fragmentWasShown = false
        val mocked = Mockito.mockConstruction(RecommendedUpdateFragment::class.java) { mock, _ ->
            mock.stub {
                on { show(any<FragmentManager>(), anyString()) } doAnswer { fragmentWasShown = true }
            }
        }
        val mockFragmentManager = mock<FragmentManager>()
        versionManager.showRecommendedFragment(mockFragmentManager)

        verify(mockLocalCache).isRecommendationExpired()
        verify(mockFragmentManager).findFragmentByTag(RecommendedUpdateFragment.screenTag)
        verify(mockLocalCache).postponeUpdateRecommendation(false)
        verify(mockAnalytics).send(eq(AnalyticsEvent.ShowRecommendedScreen()), anyOrNull(), anyOrNull())
        assertTrue(fragmentWasShown)

        clearInvocations(mockLocalCache)
        clearInvocations(mockFragmentManager)
        fragmentWasShown = false

        versionManager.showRecommendedFragment(mockFragmentManager)
        verify(mockLocalCache).isRecommendationExpired()
        verify(mockFragmentManager).findFragmentByTag(anyOrNull())
        assertTrue(fragmentWasShown)

        mocked.close()
        looperMock.close()
    }

    @Test
    fun `Show recommended fragment, postpone to next session and send analytics`() {
        val looperMock = mockLooper()

        buildVersionManager()
        isRecommendationExpired = true
        var fragmentWasShown = false
        val mocked = Mockito.mockConstruction(RecommendedUpdateFragment::class.java) { mock, _ ->
            mock.stub {
                on { show(any<FragmentManager>(), anyString()) } doAnswer { fragmentWasShown = true }
            }
        }
        val mockFragmentManager = mock<FragmentManager>()
        versionManager.showRecommendedFragment(mockFragmentManager)

        verify(mockLocalCache).isRecommendationExpired()
        verify(mockFragmentManager).findFragmentByTag(RecommendedUpdateFragment.screenTag)
        verify(mockLocalCache).postponeUpdateRecommendation(false)
        verify(mockAnalytics).send(eq(AnalyticsEvent.ShowRecommendedScreen()), anyOrNull(), anyOrNull())
        assertTrue(fragmentWasShown)
        mocked.close()
        looperMock.close()
    }

    @Test
    fun `On apply debug version save to debugStateHolder and check compatibility`() = runTest {
        buildVersionManager()
        val testVersion = "1.2.3"
        versionManager.applyDebugVersion(testVersion)

        verify(debugStateHolder).setDebugVersion(testVersion)
    }

    @Test
    fun `On apply debug status save it to debugStateHolder`() {
        buildVersionManager()
        versionManager.applyUpdateStatus(UpdateStatus.Mandatory)

        verify(debugStateHolder).setUpdateDebugStatus(UpdateStatus.Mandatory)
    }

    @Test
    fun `Do not request in app update if usePlayServiceRecommended turned off`() = runTest {
        testAppUpdateBehavior = SBIS_SERVICE_CRITICAL or SBIS_SERVICE_RECOMMENDED
        buildVersionManager()
        versionManager.init()

        verify(mockInAppUpdateChecker, never()).requestUpdateAvailable()
    }

    @Test
    fun `If request in app update returned true, emit update status recommended`() {
        buildVersionManager()
        inAppUpdateResult = true
        versionManager.init()

        assertEquals(UpdateStatus.Recommended, versionManager.state.value)
    }

    fun variousUpdateStatus() = params {
        add(UpdateStatus.Recommended)
        add(UpdateStatus.Mandatory)
    }

    private fun mockLooper(): MockedStatic<Looper> {
        val looperMock = mockStatic<Looper>()
        val mockMessageQueue = mock<MessageQueue> {
            on { addIdleHandler(any()) } doAnswer { invocationOnMock ->
                val handler = invocationOnMock.arguments.first() as MessageQueue.IdleHandler
                handler.queueIdle()
                Unit
            }
        }
        looperMock.on<Looper, MessageQueue> { Looper.myQueue() } doReturn mockMessageQueue
        return looperMock
    }

    private fun buildVersionManager(): VersionManager {
        mockHostEventObservable = BehaviorSubject.create()
        val mockLoginInterface = mock<LoginInterface> {
            on { hostEventObservable } doReturn mockHostEventObservable
        }
        val mockFeatureService = mock<SbisFeatureService> {
            on { isActive(anyString()) } doAnswer { skipRecommendationIntervalFeature }
        }

        val mockDependency = mock<VersioningDependency> {
            on { getVersioningSettings() } doAnswer {
                getSettings(appVersion, appUpdateBehavior = testAppUpdateBehavior)
            }
            on { loginInterfaceProvider } doReturn object : LoginInterface.Provider {
                override val loginInterface: LoginInterface
                    get() = mockLoginInterface
            }
            on { sbisFeatureService } doReturn mockFeatureService
        }

        settingsHolder = spy(VersioningSettingsHolder(mockDependency)).apply {
            update(sbisVersionCheckerResult)
        }

        debugStateHolder = mock {
            on { isModeOn } doAnswer { isDebugOn }
            on { getUpdateDebugStatus() } doAnswer { debugUpdateStatus }
        }
        doAnswer { debugVersion }.`when`(debugStateHolder).getDebugVersion()

        mockSbisVersionChecker = mock {
            on { update() } doReturn flow { emit(sbisVersionCheckerResult) }
        }
        mockInAppUpdateChecker = mock {
            onBlocking { requestUpdateAvailable() } doAnswer { inAppUpdateResult }
        }

        mockLocalCache = mock {
            on { isRemoteVersionSettingsExpired() } doAnswer { isRemoteSettingsExpired }
            on { isRecommendationExpired() } doAnswer { isRecommendationExpired }
        }
        versionManager = VersionManager(
            testDispatcher,
            { settingsHolder },
            { mockSbisVersionChecker },
            { mockInAppUpdateChecker },
            mockLocalCache,
            { debugStateHolder },
            mockAnalytics,
            mockDependency
        )
        return versionManager
    }
}
