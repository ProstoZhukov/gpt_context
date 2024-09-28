package ru.tensor.sbis.mediaplayer.datasource

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.ConnectionPool
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.network_native.httpclient.Server
import java.util.concurrent.TimeUnit

private const val ALLOW_CROSS_PROTOCOL_REDIRECTS = true

@UnstableApi
/**
 * Фабрика HttpDataSource для медиаплеера
 *
 * @author sa.nikitin
 */
class SbisTubeHttpDataSourceFactory(
    private val loginInterface: LoginInterface,
    private val apiService: ApiService,
    private val addAccessToken: Boolean = true
) : HttpDataSource.BaseFactory() {

    override fun createDataSourceInternal(requestProperties: HttpDataSource.RequestProperties): HttpDataSource =
        OkHttpDataSource(
            apiService.okHttpClientBuilder
                //Обходной путь постоянного таймаута, возникающего при частой перемотке видео
                //https://github.com/square/okhttp/issues/3146#issuecomment-311158567
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .connectTimeout(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .followSslRedirects(ALLOW_CROSS_PROTOCOL_REDIRECTS)
                .build(),
            Server.getUserAgent(),
            null,
            if(addAccessToken) requestProperties.addToken() else null
        )

    private fun HttpDataSource.RequestProperties.addToken(): HttpDataSource.RequestProperties =
        apply { loginInterface.token?.let { token -> set("X-SBISAccessToken", token) } }
}