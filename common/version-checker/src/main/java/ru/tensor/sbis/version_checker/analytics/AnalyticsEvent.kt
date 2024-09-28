package ru.tensor.sbis.version_checker.analytics

/**
 * Событие аналитики по использованию функционала обновления.
 *
 * @property action событие, о котором будет отправлено сообщение
 */
internal sealed class AnalyticsEvent(val action: String) {

    var updateMarket: String? = null
    var isGooglePlayAvailable: Boolean? = null

    /** Действие, о факте которого будет отправлено сообщение. */
    fun getActionNaming(): String {
        val fullAction = if (updateMarket != null) {
            action + "_" + updateMarket
        } else {
            action
        }
        return when (isGooglePlayAvailable) {
            true  -> fullAction + IS_GOOGLE_PLAY_AVAILABLE
            false -> fullAction + IS_GOOGLE_PLAY_NOT_AVAILABLE
            else  -> fullAction
        }
    }

    /** Показ окна обязательного обновления */
    class ShowCriticalScreen : AnalyticsEvent(SHOW_CRITICAL_SCREEN)

    /** Показ окна рекомендованного обновления */
    class ShowRecommendedScreen : AnalyticsEvent(SHOW_RECOMMENDED_SCREEN)

    /** Клик обновить для обязательного обновления */
    class ClickCriticalUpdate : AnalyticsEvent(CLICK_CRITICAL_UPDATE)

    /** Клик обновить для рекомендованного обновления */
    class ClickRecommendedUpdate : AnalyticsEvent(CLICK_RECOMMENDED_UPDATE)

    /** Переход на установку МП по qr-коду */
    class GoInstallApp : AnalyticsEvent(GO_INSTALL_APP)

    /** Переход к установленному МП по qr-коду */
    class GoInstalledApp : AnalyticsEvent(GO_INSTALLED_APP)

    override fun equals(other: Any?): Boolean =
        if (other is AnalyticsEvent) {
            action == other.action
        } else {
            false
        }

    override fun hashCode(): Int = action.hashCode()

    private companion object {
        const val SHOW_CRITICAL_SCREEN = "versioning_show_critical"
        const val SHOW_RECOMMENDED_SCREEN = "versioning_show_recommended"
        const val CLICK_CRITICAL_UPDATE = "versioning_click_critical"
        const val CLICK_RECOMMENDED_UPDATE = "versioning_click_recommended"
        const val GO_INSTALL_APP = "versioning_install_app"
        const val GO_INSTALLED_APP = "versioning_installed_app"
        const val IS_GOOGLE_PLAY_AVAILABLE = "_gp_available"
        const val IS_GOOGLE_PLAY_NOT_AVAILABLE = "_gp_not_available"
    }
}