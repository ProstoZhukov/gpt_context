package ru.tensor.sbis.version_checker.domain.source

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.data.UpdateSourceProxy
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.version_checker_decl.data.UpdateSource
import ru.tensor.sbis.version_checker_decl.data.UpdateSource.*
import javax.inject.Inject

/**
 * Фабрика создания команд обновления МП.
 */
internal class UpdateCommandFactory @Inject constructor(
    private val context: Application,
    private val settingsHolder: VersioningSettingsHolder,
    private val detector: UpdateSourceDetector,
    private val dependency: VersioningDependency
) {

    /**
     * Создать команду обновления [UpdateCommand] текущего МП, которая содержит интенты источников обновления,
     * установленных на устройстве и включенных в настройках приложения [VersioningSettings].
     * Так же определяет, есть ли среди источников обновления Google Play Market для аналитики.
     *
     * @param isSelfRenewing возможно ли самообновление.
     */
    fun create(
        isSelfRenewing: Boolean = true,
        onComplete: (UpdateCommand, Boolean) -> Unit
    ) {
        var hasGooglePlay = false

        val sourceIntents = detector.locateAll(isSelfRenewing).mapNotNull { source ->
            hasGooglePlay = hasGooglePlay || (source == GOOGLE_PLAY_STORE)
            buildIntent(source, settingsHolder.cleanAppId)
        }
        val command = UpdateCommand(sourceIntents)
        onComplete(command, hasGooglePlay)
    }

    /**
     * Создать команду установки/обновления [UpdateCommand] стороннего МП, которая содержит интенты источников обновления,
     * установленных на устройстве и включенных в настройках приложения [VersioningSettings].
     *
     * @param cleanAppId чистый идентификатор приложения (т.е. без возможного отладочного префикса)
     */
    fun create(cleanAppId: String): UpdateCommand {
        val sourceIntents = detector.locateAll()
            .mapNotNull { source -> buildIntent(source, cleanAppId) }
        return UpdateCommand(sourceIntents)
    }

    private fun buildIntent(source: UpdateSource, packageId: String) =
        when (source) {
            SBIS_MARKET       -> buildMarketIntent(SBIS_MARKET, packageId)
            SBIS_ONLINE       -> buildSbisOnlineIntent(packageId)
            GOOGLE_PLAY_STORE -> buildMarketIntent(GOOGLE_PLAY_STORE, packageId)
            GALAXY_STORE      -> buildMarketIntent(GALAXY_STORE, packageId)
            APP_GALLERY       -> buildMarketIntent(APP_GALLERY, packageId)
            GET_APPS          -> buildMarketIntent(GET_APPS, packageId)
            RU_STORE          -> buildMarketIntent(RU_STORE, packageId)
            NASH_STORE        -> buildMarketIntent(NASH_STORE, packageId)
            SUNMI_STORE       -> buildMarketIntent(SUNMI_STORE, packageId)
            ARMAX             -> buildMarketLaunchIntentForPackage(ARMAX)
            TIANYU            -> buildTianyuMarketIntent(packageId)
            NEWLAND           -> buildMarketLaunchIntentForPackage(NEWLAND)
        }?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

    private fun buildMarketIntent(updateSource: UpdateSource, packageId: String) =
        Intent(Intent.ACTION_VIEW).apply {
            val proxy = UpdateSourceProxy(updateSource)
            data = proxy.buildUri(packageId)
            setPackage(proxy.id)
            putSourceForAnalytics(updateSource)
        }

    private fun buildTianyuMarketIntent(packageId: String) =
        Intent(Intent.ACTION_MAIN).apply {
            val proxy = UpdateSourceProxy(TIANYU)
            setClassName(proxy.id, TIANYU_MARKET_CLASS_NAME)
            putExtra(TIANYU_MARKET_EXTRA_PACKAGE_ID_KEY, packageId)
            setPackage(proxy.id)
            putSourceForAnalytics(TIANYU)
        }

    private fun buildMarketLaunchIntentForPackage(updateSource: UpdateSource) =
        context.packageManager.getLaunchIntentForPackage(UpdateSourceProxy(updateSource).id)?.apply {
            putSourceForAnalytics(updateSource)
        }

    /**
     * Построение интента для открытия Сбис онлайн в веб-вью или браузере.
     */
    private fun buildSbisOnlineIntent(packageId: String): Intent {
        val url = UpdateSourceProxy(SBIS_ONLINE).buildUrl(packageId)
        val onlineIntent = if (dependency.webViewerFeatureProvider == null) {
            val uri = Uri.parse(url).normalizeScheme()
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER).apply {
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.takeIf {
                it.resolveActivity(context.packageManager) != null
            } ?: Intent(Intent.ACTION_VIEW).apply {
                data = uri
                putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            dependency.webViewerFeatureProvider!!.webViewerFeature
                .getDocumentViewerActivityIntentNoToolbar(context, url)
        }
        onlineIntent.putSourceForAnalytics(SBIS_ONLINE)
        return onlineIntent
    }

    private fun Intent.putSourceForAnalytics(updateSource: UpdateSource): Intent {
        putExtra(UPDATE_SOURCE_KEY, updateSource.toString())
        return this
    }

    companion object {
        /**
         * Ключ выбранного id маркета для обновления МП, используется для отправки аналитики.
         */
        const val UPDATE_SOURCE_KEY = "update_source"
        const val TIANYU_MARKET_CLASS_NAME = "com.whty.smartpos.appstore.moduleappdetail.AppDetailActivity"
        const val TIANYU_MARKET_EXTRA_PACKAGE_ID_KEY = "appId"
    }
}
