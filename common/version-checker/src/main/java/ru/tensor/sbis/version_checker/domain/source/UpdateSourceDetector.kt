package ru.tensor.sbis.version_checker.domain.source

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import ru.tensor.sbis.version_checker.data.UpdateSourceProxy
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker_decl.data.UpdateSource
import ru.tensor.sbis.version_checker_decl.data.UpdateSource.*
import javax.inject.Inject

/**
 * Детектор определения источника обновления МП [UpdateSource].
 *
 * Реализует поиск источника обновления по следующему приоритету:
 * 1. Определен исходный маркет ->
 * 2. Установлен Сбис Маркет [UpdateSource.SBIS_MARKET] ->
 * 3. Установлен Гугл Маркет [UpdateSource.GOOGLE_PLAY_STORE] ->
 * 4. Установлен маркет специфичный для устройства ->
 * 5. Портал sbis.ru/apps
 *
 * @property settingsHolder холдер настроек версионирования
 */
internal class UpdateSourceDetector @Inject constructor(
    private val context: Application,
    private val settingsHolder: VersioningSettingsHolder
) {

    private val packageManager = context.packageManager
    private val supportedSources = settingsHolder.getUpdateSource()

    /**
     * Найти все доступные источники возможного получения обновления.
     * ВЫЗОВЫ МЕТОДОВ В ПОРЯДКЕ ПРИОРИТЕТА ИСТОЧНИКА!
     *
     * @param isSelfRenewing возможно ли самообновление.
     */
    fun locateAll(isSelfRenewing: Boolean = true): List<UpdateSource> =
        mutableListOf(
            getInstallerSource(),
            getSbisMarketIfAvailable(isSelfRenewing),
            getGooglePlayStoreIfAvailable(),
            *getDeviceMarkets(),
            SBIS_ONLINE
        ).filterNotNull().distinct()

    /** Определен исходный маркет */
    private fun getInstallerSource(): UpdateSource? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(context.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION") // Проверка на версию API выполняется выше
            packageManager.getInstallerPackageName(context.packageName)
        }?.let(UpdateSourceProxy.Companion::findSource)
            // после установки могли отключить или удалить маркет
            ?.takeIfSupportedAndInstalled(supportedSources, packageManager)
    } catch (e: Exception) {
        null
    }

    /** Доступен Сбис Маркет. */
    private fun getSbisMarketIfAvailable(isSelfRenewing: Boolean): UpdateSource? {
        if (!isSelfRenewing && settingsHolder.cleanAppId == UpdateSourceProxy(SBIS_MARKET).id) {
            return null
        }
        return SBIS_MARKET.takeIfSupportedAndInstalled(supportedSources, packageManager)
    }

    /** Доступен Гугл Маркет. */
    private fun getGooglePlayStoreIfAvailable() =
        GOOGLE_PLAY_STORE.takeIfSupportedAndInstalled(supportedSources, packageManager)

    /** Доступен маркет специфичный для устройства. */
    private fun getDeviceMarkets(): Array<UpdateSource?> =
        arrayOf(
            APP_GALLERY.takeIfSupportedAndInstalled(supportedSources, packageManager),
            GET_APPS.takeIfSupportedAndInstalled(supportedSources, packageManager),
            GALAXY_STORE.takeIfSupportedAndInstalled(supportedSources, packageManager),
            RU_STORE.takeIfSupportedAndInstalled(supportedSources, packageManager),
            NASH_STORE.takeIfSupportedAndInstalled(supportedSources, packageManager),
            SUNMI_STORE.takeIfSupportedAndInstalled(supportedSources, packageManager),
            ARMAX.takeIfSupportedAndInstalled(supportedSources, packageManager),
            TIANYU.takeIfSupportedAndInstalled(supportedSources, packageManager),
            NEWLAND.takeIfSupportedAndInstalled(supportedSources, packageManager)
        )

    private fun UpdateSource.takeIfSupportedAndInstalled(
        enabledSources: List<UpdateSource>,
        packageManager: PackageManager
    ): UpdateSource? {
        // Пропускаем, если в настройках компонента отключена проверка по этому источнику обновления
        !enabledSources.contains(this) && return null

        val packageName = UpdateSourceProxy(this).id
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            this
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}
