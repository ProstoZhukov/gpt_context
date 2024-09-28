package ru.tensor.sbis.version_checker.domain.cache

import android.content.SharedPreferences
import android.os.Build
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.util.SESSION_ID
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.RemoteVersioningSettingResult
import ru.tensor.sbis.version_checker.data.Version
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.domain.cache.VersioningLocalCache.Companion.composePreferenceKey
import ru.tensor.sbis.version_checker.testUtils.getSettings
import ru.tensor.sbis.version_checker.ui.recommended.RecommendedUpdateFragment

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class VersioningLocalCacheTest {

    private companion object {
        private const val CRITICAL_KEY = "critical"
        private const val RECOMMENDED_KEY = "recommended"
        private const val SOURCE_KEY = "source"

        /** Ключ для получения даты последней загрузки файла `android_versions.json` */
        private val LAST_TIME_REMOTE_VERSIONS_UPDATE =
            VersioningLocalCache::class.java.canonicalName!! + ".version_last_time_versions_update"

        /** Ключ для получения даты для следующего показа [RecommendedUpdateFragment] */
        private val NEXT_TIME_FOR_RECOMMENDATION_KEY =
            VersioningLocalCache::class.java.canonicalName!! + ".next_recommendation_time"

        /** Ключ для получения id последнего сеанса приложения */
        private val LAST_SESSION_ID_KEY =
            VersioningLocalCache::class.java.canonicalName!! + ".version_dismiss_session_id"

        /** Ключ для показа [RecommendedUpdateFragment] по изменившемуся id сеанса приложения*/
        private val IS_UPDATE_ON_NEXT_SESSION_KEY =
            VersioningLocalCache::class.java.canonicalName!! + ".recommendation_on_next_session"
    }

    private lateinit var localCache: VersioningLocalCache
    private lateinit var settingsHolder: VersioningSettingsHolder
    private lateinit var mockPreferences: SharedPreferences
    private lateinit var mockPreferencesEditor: SharedPreferences.Editor
    private lateinit var debugState: DebugStateHolder
    private var lastTimeVersionsUpdate: Long? = null
    private var nextTimeForRecommendation: Long? = null
    private var isUpdateOnNextSession: Boolean? = null
    private var lastSessionId: String = ""
    private var debugIsOn = false
    private var skipRecommendationIntervalFeature = false
    private var result = RemoteVersioningSettingResult(Version("1.0"), Version("2.0"))

    @After
    fun tearDown() {
        lastTimeVersionsUpdate = null
        nextTimeForRecommendation = null
        isUpdateOnNextSession = null
        lastSessionId = ""
        debugIsOn = false
        skipRecommendationIntervalFeature = false
    }

    @Test
    fun `Load remote settings on init`() {
        buildLocalCache()
        verify(settingsHolder).update(any())
    }

    @Test
    fun `Remote settings expired if preferences don't contain next time for update`() {
        buildLocalCache()
        assertTrue(localCache.isRemoteVersionSettingsExpired())
    }

    @Test
    fun `Remote settings expired if passed more than 1 day`() {
        buildLocalCache()

        lastTimeVersionsUpdate = System.currentTimeMillis() - 90000000 // ~25 часов назад
        assertTrue(localCache.isRemoteVersionSettingsExpired())

        lastTimeVersionsUpdate = System.currentTimeMillis() - 82800000 // ~23 часов назад
        assertFalse(localCache.isRemoteVersionSettingsExpired())

        lastTimeVersionsUpdate = System.currentTimeMillis()
        assertFalse(localCache.isRemoteVersionSettingsExpired())
    }

    @Test
    fun `On save dictionary save critical, recommended version, source and updateTimeMills`() {
        buildLocalCache()
        localCache.saveDictionary(result)
        verify(mockPreferencesEditor).putString(composePreferenceKey(CRITICAL_KEY), result.critical?.version)
        verify(mockPreferencesEditor).putString(composePreferenceKey(RECOMMENDED_KEY), result.recommended?.version)
        verify(mockPreferencesEditor).putLong(eq(LAST_TIME_REMOTE_VERSIONS_UPDATE), any())
    }

    @Test
    fun `When next time has come, assert recommendation expired`() {
        buildLocalCache()

        nextTimeForRecommendation = System.currentTimeMillis() - 1
        assertTrue(localCache.isRecommendationExpired())
    }

    @Test
    fun `When skip check recommendation interval enabled and next time hasn't come, assert recommendation expired`() {
        buildLocalCache()

        skipRecommendationIntervalFeature = true
        nextTimeForRecommendation = System.currentTimeMillis() + 1
        assertTrue(localCache.isRecommendationExpired())
    }

    @Test
    fun `If recommendation update is postponed by button on save correct preferences`() {
        buildLocalCache()

        localCache.postponeUpdateRecommendation(true)
        verify(mockPreferencesEditor).putLong(eq(NEXT_TIME_FOR_RECOMMENDATION_KEY), any())
        verify(mockPreferencesEditor).remove(eq(IS_UPDATE_ON_NEXT_SESSION_KEY))
        verify(mockPreferencesEditor, never()).putString(eq(LAST_SESSION_ID_KEY), any())
    }

    @Test
    fun `If recommendation update is postponed not by button save correct preferences`() {
        buildLocalCache()

        localCache.postponeUpdateRecommendation(false)
        verify(mockPreferencesEditor, never()).putLong(eq(NEXT_TIME_FOR_RECOMMENDATION_KEY), any())
        verify(mockPreferencesEditor).putString(eq(LAST_SESSION_ID_KEY), any())
        verify(mockPreferencesEditor).putBoolean(eq(IS_UPDATE_ON_NEXT_SESSION_KEY), eq(true))
    }

    @Test
    fun `Recommendation time expired if update on next session and session changed`() {
        buildLocalCache()
        isUpdateOnNextSession = true

        lastSessionId = SESSION_ID.toString()
        assertFalse(localCache.isRecommendationExpired())

        lastSessionId = "123"
        assertTrue(localCache.isRecommendationExpired())
    }

    private fun buildLocalCache() {
        mockPreferencesEditor = mock {
            on { putString(anyString(), anyString()) } doReturn mock()
            on { putLong(anyString(), anyLong()) } doReturn mock()
            on { putBoolean(anyString(), anyBoolean()) } doReturn mock()
            on { remove(anyString()) } doReturn mock()
        }
        doNothing().whenever(mockPreferencesEditor).apply()
        mockPreferences = mock {
            on { edit() } doReturn mockPreferencesEditor
            on { contains(LAST_TIME_REMOTE_VERSIONS_UPDATE) } doAnswer { lastTimeVersionsUpdate != null }
            on { getLong(LAST_TIME_REMOTE_VERSIONS_UPDATE, 0L) } doAnswer { lastTimeVersionsUpdate }
            on { contains(NEXT_TIME_FOR_RECOMMENDATION_KEY) } doAnswer { nextTimeForRecommendation != null }
            on { getLong(NEXT_TIME_FOR_RECOMMENDATION_KEY, 0L) } doAnswer { nextTimeForRecommendation }
            on { getBoolean(eq(IS_UPDATE_ON_NEXT_SESSION_KEY), any()) } doAnswer { isUpdateOnNextSession }
            on { getString(eq(LAST_SESSION_ID_KEY), anyString()) } doAnswer { lastSessionId }
            on { getString(eq("version_${CRITICAL_KEY}_key"), anyOrNull()) } doReturn "0.1"
            on { getString(eq("version_${RECOMMENDED_KEY}_key"), anyOrNull()) } doReturn "0.1"
            on { getString(eq("version_${SOURCE_KEY}_key"), anyOrNull()) } doReturn "0.1"
        }
        val mockFeatureService = mock<SbisFeatureService> {
            on { isActive(anyString()) } doAnswer { skipRecommendationIntervalFeature }
        }
        val mockDependency = mock<VersioningDependency> {
            on { getVersioningSettings() } doAnswer { getSettings() }
            on { sbisFeatureService } doReturn mockFeatureService
        }

        settingsHolder = spy(VersioningSettingsHolder(mockDependency))
        debugState = mock {
            on { isModeOn } doAnswer { debugIsOn }
        }
        localCache = VersioningLocalCache(mockPreferences, settingsHolder, debugState)
    }
}
