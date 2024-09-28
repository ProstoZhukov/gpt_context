package ru.tensor.sbis.link_opener.domain.router

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import ru.tensor.sbis.link_opener.analytics.Analytics
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.link_opener.data.isKnownDocType
import ru.tensor.sbis.link_opener.di.DOMAIN_KEYWORDS
import ru.tensor.sbis.link_opener.domain.router.producer.*
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Фабрика обработчиков открытия ссылок на документы.
 *
 * @param context контекст приложения.
 * @param producers набор возможных производителей обработчиков ссылок.
 * @param analytics аналитика по использованию функционала открытия ссылок.
 * @param keywords ключевые слова для проверки, что ссылка на домен СБИС.
 *
 * @author as.chadov
 */
internal class LinkOpenHandlerFactory @Inject constructor(
    private val context: Context,
    private val producers: Map<Class<*>, @JvmSuppressWildcards Provider<LinkOpenHandlerProducer>>,
    private val analytics: Analytics,
    private val configuration: LinkOpenerFeatureConfiguration,
    @Named(DOMAIN_KEYWORDS) private val keywords: List<String>
) {
    /**
     * Получить обработчик открытия ссылки [preview].
     */
    fun create(preview: LinkPreview): LinkOpenEventHandler {
        val inAppHandler = take<InAppLinkOpenHandlerProducer>().produce(preview)
        if (isExplicitHandler(inAppHandler, preview)) {
            // сразу возвращаем явный обработчик
            analytics.sendEvent<AnalyticsEvent.OpenCardType>(preview)
            return inAppHandler!!
        }
        var inOtherHandler: LinkOpenEventHandler? = null
        if (preview.isKnownDocType && useRedirect(preview)) {
            val handler = take<OtherInAppLinkOpenHandlerProducer>().produce(preview)
            if (isExplicitHandler(handler, preview)) {
                // сразу возвращаем явный обработчик из другого МП СБИС
                analytics.sendEvent<AnalyticsEvent.OpenSabylink>(preview)
                return handler!!
            }
            inOtherHandler = handler
        }
        val handler = when {
            // принудительно перенаправляем в браузер
            shouldForceOpenInBrowser(preview) -> getBrowserEventHandler(preview)
            // неявный обработчик текущего МП
            inAppHandler != null      -> {
                analytics.sendEvent<AnalyticsEvent.OpenCardType>(preview)
                inAppHandler
            }
            // неявный обработчик из другого МП
            inOtherHandler != null    -> {
                analytics.sendEvent<AnalyticsEvent.OpenSabylink>(preview)
                inOtherHandler
            }
            // обработчик в WebView текущего МП
            isLinkForWebView(preview) -> {
                analytics.sendEvent<AnalyticsEvent.OpenWebViewType>(preview)
                take<OurLinkOpenHandlerProducer>().produce(preview)
            }
            // обработчик в Chrome custom tabs текущего МП
            shouldOpenInCustomTab()   -> {
                analytics.sendEvent<AnalyticsEvent.OpenCustomTabsType>(preview)
                take<TabsLinkOpenHandlerProducer>().produce(preview)
            }
            // обработчик в браузере
            else -> getBrowserEventHandler(preview)
        }
        return handler
    }

    /**
     * Получить обработчик открытия ссылки в веб-вью.
     */
    fun createWebViewHandler(link: LinkPreview): LinkOpenEventHandler =
        take<OurLinkOpenHandlerProducer>().produce(link)

    private fun useRedirect(preview: LinkPreview): Boolean {
        if (!preview.isSabylink && configuration.useSabylinkAppRedirect) {
            return true
        }
        if (!preview.isOuter && !preview.isSabylink && configuration.useInnerAppRedirect) {
            return true
        }
        return false
    }

    /**
     * Является ли обработчик [handler] явным, т.е. подходящим по всем критериям для данной ссылки [preview]:
     * - Обрабатывает тип и подтип ссылки
     * - Обрабатывает тип и любой подтип, но только если из ссылки он не получен
     * - Не имеет низкий приоритет
     */
    private fun isExplicitHandler(handler: LinkOpenEventHandler?, preview: LinkPreview) = when {
        handler == null -> false
        handler.priority == LinkOpenHandlerPriority.LOW -> false
        preview.docSubtype != LinkDocSubtype.UNKNOWN && handler.subtypes.contains(preview.docSubtype) -> true
        preview.docSubtype == LinkDocSubtype.UNKNOWN && handler.subtypes.isEmpty() -> true
        else -> false
    }

    private fun isLinkForWebView(linkPreview: LinkPreview): Boolean {
        linkPreview.docType != DocType.UNKNOWN && return true
        val uri = Uri.parse(linkPreview.href)
        var host = uri.host.orEmpty()
        if (host.startsWith(REDUNDANT_WWW_SERVICE_DOMAIN, true)) {
            host = host.replace(REDUNDANT_WWW_SERVICE_DOMAIN, "", true)
        }
        val isSbisResource = keywords.any(host::contains)
        if (isSbisResource && uri.scheme.orEmpty() == UNSAFE_NETWORK_PROTOCOL) {
            Timber.e("Попытка открыть ссылку на сбис ресурс использующий неподдерживаемый протокол http: ${linkPreview.href}")
            return false
        }
        return isSbisResource
    }

    private fun shouldForceOpenInBrowser(preview: LinkPreview) = preview.fullUrl.contains(DISCOUNT_CARD_URL_PATTERN)

    private fun shouldOpenInCustomTab(): Boolean {
        !configuration.areCustomTabsAllowed && return false

        return getCustomTabsPackages(context).isNotEmpty()
    }

    /**
     * Найти браузеры поддерживающие Custom Tabs.
     * https://developer.chrome.com/docs/android/custom-tabs/integration-guide/#how-can-i-check-whether-the-android-device-has-a-browser-that-supports-custom-tab
     */
    private fun getCustomTabsPackages(context: Context): List<ResolveInfo> {
        val packagesWithCustomTabs = mutableListOf<ResolveInfo>()
        // Кто поддерживает Intent.ACTION_VIEW
        val resolvedActivityList = context.packageManager.queryIntentActivities(viewIntent, 0)
        resolvedActivityList.forEach { info ->
            val serviceIntent = Intent().apply {
                setPackage(info.activityInfo.packageName)
                action = ACTION_CUSTOM_TABS_CONNECTION
            }
            // Поддерживает ли предположительный бразер Custom Tabs
            if (context.packageManager.resolveService(serviceIntent, 0) != null) {
                packagesWithCustomTabs.add(info)
            }
        }

        return packagesWithCustomTabs.also {
            Timber.d("Found ${packagesWithCustomTabs.size} browsers with Custom Tabs")
        }
    }

    private fun getBrowserEventHandler(preview: LinkPreview): LinkOpenEventHandler {
        analytics.sendEvent<AnalyticsEvent.OpenBrowserType>(preview)
        return take<ForeignLinkOpenHandlerProducer>().produce(preview)
    }

    private inline fun <reified T> take(): T =
        producers.getValue(T::class.java).get() as T

    /** Дефолтный интент на просмотр. */
    private val viewIntent by lazy {
        Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts(UNSAFE_NETWORK_PROTOCOL, "", null))
    }

    /**
     * Интерфейс производителя обработчика ссылок [LinkOpenEventHandler].
     */
    interface LinkOpenHandlerProducer {

        /**
         * Получить обработчик открытия ссылки.
         */
        fun produce(preview: LinkPreview): LinkOpenEventHandler?
    }

    private companion object {
        /** Избыточный домен сервиса информационной системы. */
        const val REDUNDANT_WWW_SERVICE_DOMAIN = "www."

        /** @SelfDocumented */
        const val UNSAFE_NETWORK_PROTOCOL = "http"

        /** @SelfDocumented */
        const val DISCOUNT_CARD_URL_PATTERN = "discount-card-image"
    }
}
