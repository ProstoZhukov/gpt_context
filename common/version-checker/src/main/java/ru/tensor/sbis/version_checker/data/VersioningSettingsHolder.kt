package ru.tensor.sbis.version_checker.data

import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus.Empty
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus.Mandatory
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus.Recommended
import javax.inject.Inject

/**
 * Холдер локальных [VersioningSettings] и удаленных [RemoteVersioningSettingResult] настроек версионирования.
 *
 * @property cleanAppId чистый идентификатор приложения (т.е. без возможного отладочного префикса)
 * @property remote удаленные (облачные) настройки версионирования
 *
 * @author as.chadov
 */
@VersioningSingletonScope
internal class VersioningSettingsHolder @Inject constructor(
    private val dependency: VersioningDependency
) : VersioningSettings by dependency.getVersioningSettings() {

    val cleanAppId = appId.replace(APP_DEBUG_SUFFIX, "", true)

    val checkRecommendationInterval get() =
        dependency.sbisFeatureService?.isActive(SKIP_RECOMMENDATION_INTERVAL_FEATURE)?.not() ?: true

    var remote = RemoteVersioningSettingResult.empty()
        private set

    /** Обновить удаленные настройки. */
    fun update(result: RemoteVersioningSettingResult) {
        remote = result
    }

    /** @SelfDocumented */
    fun remoteVersionFor(status: UpdateStatus) = when (status) {
        Recommended -> remote.recommended
        Mandatory -> remote.critical
        Empty -> null
    }

    companion object {
        /** Дебажный суфикс applicationDebugSuffix */
        const val APP_DEBUG_SUFFIX = ".debug"

        /**
         * Показываем рекомендательные обновления при каждом входе, если включена фича
         * https://dev.sbis.ru/opendoc.html?guid=34bcd26b-b185-4370-af8c-1c48241f2b4b&client=3
         * */
        private const val SKIP_RECOMMENDATION_INTERVAL_FEATURE = "mobile_version_check"
    }
}
