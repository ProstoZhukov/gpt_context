package ru.tensor.sbis.version_checker.domain.utils

import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.version_checker.domain.service.VersionServiceChecker
import ru.tensor.sbis.version_checker.domain.service.InAppUpdateChecker
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.PLAY_SERVICE_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_CRITICAL
import ru.tensor.sbis.version_checker_decl.data.AppUpdateBehavior.Companion.SBIS_SERVICE_RECOMMENDED

/**
 * Включен ли в настройках приложения [VersioningSettings] функционал критического обновления
 * с использованием [VersionServiceChecker].
 */
internal fun VersioningSettings.useSbisRecommended() = use(SBIS_SERVICE_RECOMMENDED)

/**
 * Включен ли в настройках приложения [VersioningSettings] функционал рекомендуемого обновления
 * с использованием [VersionServiceChecker].
 */
internal fun VersioningSettings.useSbisCritical() = use(SBIS_SERVICE_CRITICAL)

/**
 * Включен ли в настройках приложения [VersioningSettings] функционал рекомендуемого обновления
 * с использованием [InAppUpdateChecker].
 */
internal fun VersioningSettings.usePlayServiceRecommended() = use(PLAY_SERVICE_RECOMMENDED)

private fun VersioningSettings.use(flag: Int): Boolean {
    val bitmask = getAppUpdateBehavior()
    return (bitmask and flag) == flag
}