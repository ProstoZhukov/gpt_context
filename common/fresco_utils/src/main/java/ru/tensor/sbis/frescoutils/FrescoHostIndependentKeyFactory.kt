package ru.tensor.sbis.frescoutils

import android.net.Uri
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import ru.tensor.sbis.network_native.httpclient.Server

/**
 * Реализация [DefaultCacheKeyFactory], отрезающая хост из [Uri], если он - один из элементов [Server.Host]
 * Требуется для сохранения актуальности кэша изображений при переключении со sbis хостов на aby хосты и обратно
 */
object FrescoHostIndependentKeyFactory : DefaultCacheKeyFactory() {

    override fun getCacheKeySourceUri(sourceUri: Uri): Uri = canonicalUri(sourceUri)

    fun canonicalUri(sourceUri: Uri): Uri =
        cutSbisHostFromUri(sourceUri)?.let(Uri::parse) ?: super.getCacheKeySourceUri(sourceUri)

    /**
     * Возвращает строковое значение [Uri] без хоста
     */
    fun cutSbisHostFromUri(uri: Uri): String? {
        val host: String? = uri.host
        return if (host != null && isServiceHost(host)) {
            val fullHost = uri.scheme?.let { "$it://$host" } ?: host
            uri.toString().replace(fullHost, "")
        } else {
            null
        }
    }

    private fun isServiceHost(host: String) = Server.Host.values().any { host == it.hostUrl || host == it.mirror }
}