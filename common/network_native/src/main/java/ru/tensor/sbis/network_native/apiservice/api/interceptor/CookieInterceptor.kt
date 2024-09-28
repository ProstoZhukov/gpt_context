package ru.tensor.sbis.network_native.apiservice.api.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.tensor.sbis.network_native.apiservice.api.SessionIdProvider
import timber.log.Timber
import java.io.IOException


/**
 * Interceptor для добавления идентификатора сессии в заголовки сетевого запроса
 *
 * @property sessionIdProvider Провайдер идентификатора сессии
 */
@Suppress("unused")
class CookieInterceptor(private val sessionIdProvider: SessionIdProvider) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addTokenToHeader(sessionIdProvider.getTokenId())
                .build()
        )
    }

    private fun Request.Builder.addTokenToHeader(tokenId: String?) = apply {
        if (tokenId != null) addHeader("X-SBISAccessToken", tokenId)
        else Timber.e("Expected that tokenId is not null, but it is.")
    }
}
