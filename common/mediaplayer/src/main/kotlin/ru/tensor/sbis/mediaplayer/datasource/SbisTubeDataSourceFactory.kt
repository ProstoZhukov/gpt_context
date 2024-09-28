package ru.tensor.sbis.mediaplayer.datasource

import android.content.Context
import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import ru.tensor.sbis.mediaplayer.cache.SbisTubeCacheRegistry
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.io.File

@UnstableApi
/**
 * Фабрика DataSource для медиаплеера
 *
 * @param context       Контекст
 * @param cacheDir      Директория кеша
 *
 * @author sa.nikitin
 */
class SbisTubeDataSourceFactory(
    context: Context,
    private val loginInterface: LoginInterface,
    private val apiService: ApiService,
    private val initialSourceUri: Uri,
    cacheDir: File?,
    private val addAccessToken: Boolean = true
) : DataSource.Factory {

    private val cacheFacade: SbisTubeCacheRegistry.SbisTubeCache.Facade? = createCacheFacadeOrNull(context, cacheDir)
    private val dataSourceFactory: DataSource.Factory =
        if (cacheFacade != null) {
            createCacheDataSourceFactory(cacheFacade, createDefaultDataSourceFactory(context))
        } else {
            createDefaultDataSourceFactory(context)
        }

    private fun createCacheFacadeOrNull(
        context: Context,
        cacheDir: File?
    ): SbisTubeCacheRegistry.SbisTubeCache.Facade? =
        if (cacheDir != null) {
            SbisTubeCacheRegistry.INSTANCE.getCache(context, cacheDir)
        } else {
            null
        }

    private fun createDefaultDataSourceFactory(context: Context): DataSource.Factory =
        DefaultDataSourceFactory(
            context,
            DefaultBandwidthMeter.Builder(context).build(),
            createResolvingDataSourceFactory(
                SbisTubeHttpDataSourceFactory(loginInterface, apiService, addAccessToken),
                initialSourceUri
            )
        )

    private fun createCacheDataSourceFactory(
        cacheFacade: SbisTubeCacheRegistry.SbisTubeCache.Facade,
        upstreamDataSourceFactory: DataSource.Factory
    ): DataSource.Factory =
        CacheDataSource.Factory().apply {
            setCache(cacheFacade.cache())
            setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }

    override fun createDataSource(): DataSource = dataSourceFactory.createDataSource()

    /**
     * Метод для освобождения занятых ресурсов, например, освобождения директории кэша
     * Фабрика не должна использоваться после вызова этого метода
     */
    fun release() {
        cacheFacade?.release()
    }
}