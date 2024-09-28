package ru.tensor.sbis.link_opener.analytics

/**
 * Событие аналитики по использованию функционала открытия ссылок.
 *
 * @property context контекст использования.
 * @property docType тип документа.
 * @property docSubtype подтип документа.
 *
 * @author al.kropotov
 */
internal sealed class AnalyticsEvent(
    val context: String
) {

    var docType = ""
    var docSubtype = ""

    /** Действие, о факте которого будет отправлено сообщение. */
    fun getAction(): String = LINK_OPENER_ACTION + "_" + docType + "_" + docSubtype

    /** Открытие ссылки через карточку в приложении. */
    class OpenCardType : AnalyticsEvent(LINK_OPENER_CARD_KEY)

    /** Открытие sabylink-ссылки ([OpenCardType] после редиректа из нецелевого МП). */
    class OpenSabylink : AnalyticsEvent(LINK_OPENER_SABYLINK_KEY)

    /** Способ открытия ссылки через customTabs. */
    class OpenCustomTabsType : AnalyticsEvent(LINK_OPENER_CUSTOMTABS_KEY)

    /** Способ открытия ссылки через webView. */
    class OpenWebViewType : AnalyticsEvent(LINK_OPENER_WEBVIEW_KEY)

    /** Способ открытия ссылки через внешний браузер. */
    class OpenBrowserType : AnalyticsEvent(LINK_OPENER_BROWSER_KEY)

    private companion object {
        const val LINK_OPENER_CARD_KEY = "link_opener_card"
        const val LINK_OPENER_SABYLINK_KEY = "link_opener_sabylink"
        const val LINK_OPENER_CUSTOMTABS_KEY = "link_opener_customTabs"
        const val LINK_OPENER_WEBVIEW_KEY = "link_opener_webView"
        const val LINK_OPENER_BROWSER_KEY = "link_opener_browser"
        const val LINK_OPENER_ACTION = "open"
    }
}
