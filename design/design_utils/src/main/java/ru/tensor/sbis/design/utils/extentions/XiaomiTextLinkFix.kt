/**
 * Инструмент для исправления специфической ошибки на устройствах Xiaomi
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.utils.extentions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.util.Linkify
import android.widget.TextView
import java.util.*

private const val XIAOMI_MANUFACTURER = "xiaomi"
private const val WEB_VIEW_PACKAGE = "com.google.android.webview"

private val isXiaomi: Boolean = Build.MANUFACTURER.lowercase(Locale.ROOT).contains(XIAOMI_MANUFACTURER)
private var isWebViewInstalledAndEnabled: Boolean? = null

/**
 * Исправление ссылок в TextView для устройств фирмы Xiaomi
 *
 * * Проблема 1: кривая обработка лонгклика на устройствах этой фирмы.
 * Решается выставлением [Linkify.ALL] и установкой слушателя лонгклика.
 * [Ссылка на ошибку](https://online.sbis.ru/opendoc.html?guid=b43b1f55-6d4c-49ae-839c-ba4ea90cd100)
 *
 * * Проблема 2: если выставлен [Linkify.ALL], случается краш, если на устройстве не установлено приложение WebView,
 * решается установкой [Linkify.WEB_URLS]
 * [Ссылка на ошибку](https://online.sbis.ru/doc/f3d7f9f0-8ba6-4d8f-8c48-94a6d781676d)
 */
@Suppress("unused")
fun TextView.fixLinksForXiaomi() {
    if (!isXiaomi) {
        return
    }

    autoLinkMask = if (hasWebView(context)) {
        Linkify.ALL
    } else {
        Linkify.WEB_URLS
    }

    setOnLongClickListener {
        return@setOnLongClickListener true
    }
}

private fun hasWebView(context: Context): Boolean {
    if (isWebViewInstalledAndEnabled != null) {
        return isWebViewInstalledAndEnabled!!
    }

    val packageManager = context.packageManager

    isWebViewInstalledAndEnabled =
        try {
            packageManager.getApplicationInfo(WEB_VIEW_PACKAGE, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    return isWebViewInstalledAndEnabled!!
}
