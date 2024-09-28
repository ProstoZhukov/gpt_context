package ru.tensor.sbis.version_checker.domain.service

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.UpdateAvailability
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.testUtils.TEST_APP_ID
import java.util.concurrent.Executor

@Suppress("unused", "JUnitMalformedDeclaration")
@ExperimentalCoroutinesApi
@RunWith(JUnitParamsRunner::class)
internal class InAppUpdateCheckerTest {

    private var testAppUpdateInfo = spy(createTestAppUpdateInfo())
    private var debugRecommendedIntervalDays = 0
    private var releaseRecommendedIntervalDays = 7
    private var isDebug = false
    private var mockAppUpdateManager = spy(TestAppUpdateManager())
    private lateinit var mockStaticAppUpdateFactory: MockedStatic<AppUpdateManagerFactory>
    private lateinit var mockStaticAppConfig: MockedStatic<AppConfig>
    private lateinit var inAppUpdateChecker: InAppUpdateChecker
    private lateinit var mockSettingsHolder: VersioningSettingsHolder

    @After
    fun tearDown() {
        mockStaticAppUpdateFactory.close()
        mockStaticAppConfig.close()
    }

    @Test
    @Parameters(method = "getSuccessfulCases")
    fun `App update available`(debugIsOn: Boolean, days: Int): Unit = runBlocking {
        buildInAppUpdateChecker()

        isDebug = debugIsOn
        testAppUpdateInfo = spy(
            createTestAppUpdateInfo(
                updateAvailability = UpdateAvailability.UPDATE_AVAILABLE,
                clientVersionStalenessDays = days
            )
        )

        assertTrue(inAppUpdateChecker.requestUpdateAvailable())
        verify(testAppUpdateInfo, times(2)).updateAvailability()
        verify(testAppUpdateInfo, times(2)).clientVersionStalenessDays()
        verify(testAppUpdateInfo).availableVersionCode()
    }

    @Test
    @Parameters(method = "getFailureCases")
    fun `App update unavailable`(updateAvailability: Int, debugIsOn: Boolean, days: Int): Unit = runBlocking {
        buildInAppUpdateChecker()

        isDebug = debugIsOn
        testAppUpdateInfo = spy(
            createTestAppUpdateInfo(
                updateAvailability = updateAvailability,
                clientVersionStalenessDays = days
            )
        )

        assertFalse(inAppUpdateChecker.requestUpdateAvailable())
        verify(testAppUpdateInfo, times(2)).updateAvailability()
        verify(testAppUpdateInfo, atLeastOnce()).clientVersionStalenessDays()
        verify(testAppUpdateInfo).availableVersionCode()
    }

    private fun buildInAppUpdateChecker() {
        mockAppUpdateManager = spy(TestAppUpdateManager())
        mockStaticAppUpdateFactory = mockStatic {
            on<AppUpdateManager> { AppUpdateManagerFactory.create(any()) } doReturn mockAppUpdateManager
        }
        mockStaticAppConfig = mockStatic {
            on<Boolean> { AppConfig.isDebug() } doReturn isDebug
        }
        mockSettingsHolder = mock {
            on { getRecommendedInterval() } doAnswer {
                if (isDebug) debugRecommendedIntervalDays else releaseRecommendedIntervalDays
            }
        }
        inAppUpdateChecker =
            InAppUpdateChecker(mock(), mockSettingsHolder, UnconfinedTestDispatcher())
    }

    private fun getSuccessfulCases() = params {
        add(false, 7)
        add(false, 20)
        add(true, 0)
        add(true, 20)
    }

    private fun getFailureCases() = params {
        add(UpdateAvailability.UPDATE_AVAILABLE, false, -1)
        add(UpdateAvailability.UPDATE_AVAILABLE, true, -1)
        add(UpdateAvailability.UPDATE_NOT_AVAILABLE, false, 10)
        add(UpdateAvailability.UPDATE_NOT_AVAILABLE, true, 7)
    }

    private fun createTestAppUpdateInfo(
        updateAvailability: Int = 0,
        clientVersionStalenessDays: Int? = null
    ): AppUpdateInfo {
        return AppUpdateInfo.zzb(
            TEST_APP_ID,
            400,
            updateAvailability,
            0,
            clientVersionStalenessDays,
            0,
            0L,
            0L,
            0L,
            0L,
            null,
            null,
            null,
            null,
            null
        )
    }

    // AppUpdateManager.getAppUpdateInfo() - extension-функция, поэтому остается замокать только так
    inner class TestAppUpdateManager : AppUpdateManager {
        override fun registerListener(listener: InstallStateUpdatedListener) {}
        override fun unregisterListener(listener: InstallStateUpdatedListener) {}
        override fun getAppUpdateInfo(): Task<AppUpdateInfo> = TestTaskAppUpdateInfo()
        override fun startUpdateFlow(
            appUpdateInfo: AppUpdateInfo,
            activity: Activity,
            options: AppUpdateOptions
        ): Task<Int> = mock()

        override fun startUpdateFlowForResult(
            appUpdateInfo: AppUpdateInfo,
            appUpdateType: Int,
            activity: Activity,
            requestCode: Int
        ) = false

        override fun startUpdateFlowForResult(
            appUpdateInfo: AppUpdateInfo,
            activity: Activity,
            options: AppUpdateOptions,
            requestCode: Int
        ) = false

        override fun startUpdateFlowForResult(
            appUpdateInfo: AppUpdateInfo,
            appUpdateType: Int,
            starter: IntentSenderForResultStarter,
            requestCode: Int
        ) = false

        override fun startUpdateFlowForResult(
            appUpdateInfo: AppUpdateInfo,
            starter: IntentSenderForResultStarter,
            options: AppUpdateOptions,
            requestCode: Int
        ) = false

        override fun startUpdateFlowForResult(
            p0: AppUpdateInfo,
            p1: ActivityResultLauncher<IntentSenderRequest>,
            p2: AppUpdateOptions
        ): Boolean = false

        override fun completeUpdate(): Task<Void> = mock()
        fun requestAppUpdateInfo() = testAppUpdateInfo
    }

    inner class TestTaskAppUpdateInfo : Task<AppUpdateInfo>() {
        override fun isComplete() = true
        override fun isSuccessful() = true
        override fun isCanceled(): Boolean = false
        override fun getResult(): AppUpdateInfo = testAppUpdateInfo
        override fun <X : Throwable?> getResult(exceptionType: Class<X>): AppUpdateInfo = testAppUpdateInfo
        override fun getException(): Exception = mock()
        override fun addOnSuccessListener(listener: OnSuccessListener<in AppUpdateInfo>): Task<AppUpdateInfo> = mock()
        override fun addOnSuccessListener(
            executor: Executor,
            listener: OnSuccessListener<in AppUpdateInfo>
        ): Task<AppUpdateInfo> = mock()

        override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AppUpdateInfo>): Task<AppUpdateInfo> =
            mock()

        override fun addOnFailureListener(listener: OnFailureListener): Task<AppUpdateInfo> = mock()
        override fun addOnFailureListener(executor: Executor, listener: OnFailureListener): Task<AppUpdateInfo> = mock()
        override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AppUpdateInfo> = mock()
        override fun addOnCompleteListener(listener: OnCompleteListener<AppUpdateInfo>): Task<AppUpdateInfo> = mock()
        override fun addOnCompleteListener(
            executor: Executor,
            listener: OnCompleteListener<AppUpdateInfo>
        ): Task<AppUpdateInfo> = mock()
    }
}