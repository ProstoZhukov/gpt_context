package ru.tensor.sbis.version_checker.domain.service

import android.app.Application
import androidx.annotation.AnyThread
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.di.IoDispatcher
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import ru.tensor.sbis.version_checker.domain.cache.DebugStateHolder
import timber.log.Timber
import javax.inject.Inject

@VersioningSingletonScope
internal class InAppUpdateChecker @Inject constructor(
    private val context: Application,
    private val settingsHolder: VersioningSettingsHolder,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Получение информации об опубликованной версии приложения на Google Play Market:
     * - Доступно ли обновление
     * - Сколько дней прошло с момента новой версии
     * @return true, если обновление доступно и прошло необходимое количество дней, в зависимости
     * от того, включен режим отладки или нет.
     */
    @AnyThread
    suspend fun requestUpdateAvailable(): Boolean =
        withContext(ioDispatcher) {
            Timber.d("Requesting google play update...")
            val info = createAppUpdateInfoTask() ?: return@withContext false
            val isAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                (info.clientVersionStalenessDays() ?: -1) >= daysForUpdate()
            logUpdateInfo(info, isAvailable)
            isAvailable
        }

    private suspend fun createAppUpdateInfoTask(): AppUpdateInfo? =
        try {
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            appUpdateManager.requestAppUpdateInfo()
        } catch (e: Exception) {
            Timber.d(e)
            null
        }

    private fun daysForUpdate(): Int =
        if (AppConfig.isDebug()) {
            DebugStateHolder.RECOMMENDED_INTERVAL_IN_DAYS
        } else {
            settingsHolder.getRecommendedInterval()
        }

    private fun logUpdateInfo(info: AppUpdateInfo, isAvailable: Boolean) {
        val debugInfo = "updateAvailability = ${info.updateAvailability()}, " +
            "availableVersionCode = ${info.availableVersionCode()}, " +
            "clientVersionStalenessDays = ${info.clientVersionStalenessDays()}, " +
            "daysForUpdate = ${daysForUpdate()}, isAvailable = $isAvailable"
        Timber.d(debugInfo)
    }
}
