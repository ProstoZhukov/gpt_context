package ru.tensor.sbis.common.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.GET_RESOLVED_FILTER
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.net.Uri
import android.os.Build
import android.provider.Browser
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.FileUriUtil.Companion.checkFileScheme
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import timber.log.Timber

/**
 * Открытие документа с идентификатором [uuid] в СТОРОННЕМ ПРИЛОЖЕНИИ БРАУЗЕРА.
 * Обертка над [openInBrowserApp].
 * Подробнее о назначении метода [openInBrowserApp].
 */
fun openDocumentInBrowserApp(
    context: Context,
    uuid: String,
    @StringRes errorRes: Int = R.string.common_open_link_browser_error,
    fallbackHandler: (() -> Unit)? = null
) {
    val url = buildLink(DEFAULT_OPEN_DOC_PATH, uuid)
    if (url != null) {
        openInBrowserApp(context, url, errorRes, fallbackHandler)
    } else {
        SbisPopupNotification.pushToast(context, errorRes)
    }
}

/**
 * Открытие ссылки [url] в стороннем приложении браузера выбранном в настройках устройства по умолчанию (не гарантировано).
 * Предотвращает открытие наших веб-сайтов в приложениях семейства Сбис с настроеной верефикацией по одобренным
 * доменам цифрового актива компании.
 * Если не удастся определить интернет-браузер по умолчанию будет создан селектор для предоставления выбора приложения пользователю.
 *
 * @param url адрес ресурса в интернете
 * @param errorRes сообщение об ошибке если не удается найти приложение для выполнения открытия ссылки
 * @param fallbackHandler запасной обработчик вызываемый если браузер не установлен или отключен
 */
fun openInBrowserApp(
    context: Context,
    url: String,
    @StringRes errorRes: Int = R.string.common_open_link_browser_error,
    fallbackHandler: (() -> Unit)? = null
) {
    if (checkFileScheme(url)) {
        FileOpenUtil.openExternalFile(context, url)
        return
    }
    val browserIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url).normalizeScheme()
        addCategory(Intent.CATEGORY_BROWSABLE)
        putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val defaultInfo = context.packageManager.resolveActivity(browserIntent, GET_RESOLVED_FILTER or MATCH_DEFAULT_ONLY)
    val isCurrentApp = context.packageName == defaultInfo?.activityInfo?.packageName
    val isSystemResolver = DEFAULT_SYSTEM_RESOLVER_ACTIVITY == defaultInfo?.activityInfo?.name
    if (defaultInfo == null || isCurrentApp || isSystemResolver || !defaultInfo.filter.isBrowserApp()) {
        val ignoredSabyComponent = defaultInfo?.run { ComponentName(activityInfo.packageName, activityInfo.name) }
        val specificsIntent = Intent(browserIntent).apply {
            addCategory(Intent.CATEGORY_APP_BROWSER)
        }
        // получаем набор компонентов способных обработать интент
        @Suppress("USELESS_ELVIS") val resolvers = context.packageManager.queryIntentActivityOptions(
            ignoredSabyComponent,
            arrayOf(specificsIntent),
            browserIntent,
            GET_RESOLVED_FILTER
        ) ?: emptyList()
        val resolver = resolvers.filter { info ->
            // исключаем из набора компоненты связанные со сбис МП
            SABY_PACKAGE_SECTIONS.none { info.activityInfo.packageName.contains(it, false) }
        }.let { infoList ->
            // ищем приложение интернет-браузера или берем первый доступный компонент
            infoList.firstOrNull { it.filter.isBrowserApp() } ?: infoList.firstOrNull()
        }
        // явно назначаем найденный компонент обработчиком интента
        if (resolver != null) {
            val (name, packageName) = resolver.let { it.activityInfo.name to it.activityInfo.packageName }
            Timber.i("Found component $name of browser app  $packageName to open url")
            if (packageName == CHROME_PACKAGE || name == CHROME_INTENT_DISPATCHER) {
                browserIntent.setPackage(resolver.activityInfo.packageName)
            } else {
                browserIntent.setClassName(resolver.activityInfo.packageName, resolver.activityInfo.name)
            }
        } else {
            browserIntent.putExtra(OpenLinkController.FORCED_NAVIGATION_TO_WEBVIEW, true)
            Timber.i("Not found browser app component to open url")
        }
    }

    context.startIntent(browserIntent, errorRes) {
        val spareIntent = createSpareIntent(browserIntent)
        context.startIntent(spareIntent, errorRes, fallbackHandler)
    }
}

/**
 * Создаем селектор в попытках воспользоваться выбором приложения интернет-браузера.
 * Для андроид 13 селектор [Intent.ACTION_MAIN] может работать некорректно, поэтому используем обходное решение с обширным [Uri].
 * Подробнее на https://issuetracker.google.com/issues/243678703
 */
private fun createSpareIntent(intent: Intent): Intent =
    // sdk 33 пока недоступен в Build.VERSION_CODES
    if (Build.VERSION.SDK_INT >= 33) {
        val emptyBrowserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
        intent.apply {
            selector = emptyBrowserIntent
        }
    } else {
        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER).apply {
            data = intent.data
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

/** @SelfDocumented */
private fun Context.startIntent(
    intent: Intent,
    @StringRes errorRes: Int,
    workaround: (() -> Unit)? = null
) = try {
    startActivity(intent)
} catch (e: Exception) {
    Timber.e("Fail to open link '${intent.data}' in activity ${intent.component?.className.orEmpty()} browser app ${intent.component?.packageName.orEmpty()}")
    when {
        workaround != null                  -> workaround()
        errorRes != ResourcesCompat.ID_NULL -> SbisPopupNotification.pushToast(this, errorRes)
        else                                -> Unit
    }
}

/**
 * Возвращает true если этот фильтр обрабатывает все URI данных по схеме HTTP или HTTPS.
 * Является ожидаемым поведением для приложения интернет-браузера.
 * Подробнее: https://android.googlesource.com/platform/frameworks/base/+/413020a6ca6e7d4eb7e61e3fe7d7a4c570a605db/core/java/android/content/IntentFilter.java#534
 */
private fun IntentFilter?.isBrowserApp(): Boolean {
    if (this == null) return false
    if (hasCategory(Intent.CATEGORY_APP_BROWSER)) {
        return true
    }
    if (!hasAction(Intent.ACTION_VIEW) || !hasCategory(Intent.CATEGORY_BROWSABLE) || countDataSchemes() == 0) {
        return false
    }
    var webDataURISize = 0
    for (i in 0 until countDataSchemes()) {
        val scheme = getDataScheme(i)
        if (SCHEME_HTTP == scheme || SCHEME_HTTPS == scheme) {
            webDataURISize += 1
        }
    }
    return webDataURISize == 2 && countDataAuthorities() == 0
}

/**@SelfDocumented */
private fun buildLink(vararg parts: String): String? {
    val builder = StringBuilder()
    for (part in parts) {
        builder.append(part)
    }
    val url = builder.toString()
    return UrlUtils.formatUrl(url)
}

private val SABY_PACKAGE_SECTIONS = listOf(".sbis", ".saby", ".setty")
private const val SCHEME_HTTP = "http"
private const val SCHEME_HTTPS = "https"
private const val DEFAULT_OPEN_DOC_PATH = "/opendoc.html?guid="
private const val DEFAULT_SYSTEM_RESOLVER_ACTIVITY = "com.android.internal.app.ResolverActivity"
private const val CHROME_PACKAGE = "com.android.chrome"

// Не устанавливать компонент диспетчера Chrome явным обработчиком нашего интента, может выполнить обратное перенаправление в
// приложение с объявленной верефикацией адресов.
private const val CHROME_INTENT_DISPATCHER = "com.google.android.apps.chrome.IntentDispatcher"