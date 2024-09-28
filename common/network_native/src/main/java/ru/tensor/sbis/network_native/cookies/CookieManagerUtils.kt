/**
 * Инструменты, относящиеся к `android.webkit.CookieManager`.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.network_native.cookies

import android.webkit.CookieManager
import timber.log.Timber

/**
 * Возвращает nullable [CookieManager], который может быть недоступен на устройстве без WebView.
 */
fun getCookieManagerSafe() = try {
    CookieManager.getInstance()
} catch (e: Exception) {
    Timber.w(e, "Cannot get CookieManager")
    null
}


/**
 * Очищает [CookieManager], учитывая возможность недоступности WebView на устройстве.
 */
fun clearCookiesSafe() {
    getCookieManagerSafe()?.apply {
        removeAllCookies(null)
        flush()
    }
}