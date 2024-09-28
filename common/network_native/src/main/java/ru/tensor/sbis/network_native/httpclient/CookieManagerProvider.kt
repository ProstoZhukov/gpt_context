package ru.tensor.sbis.network_native.httpclient

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет компонент [CookieManager]
 *
 * @author ds.vershinin
 */
fun interface CookieManagerProvider : Feature {
    fun cookieManager(): CookieManager
}