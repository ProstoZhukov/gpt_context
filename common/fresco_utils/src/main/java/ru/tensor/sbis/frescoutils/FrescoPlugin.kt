package ru.tensor.sbis.frescoutils

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.network_native.apiservice.api.SessionIdProvider
import ru.tensor.sbis.network_native.apiservice.api.interceptor.CookieInterceptor
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин для просмотра картинок на базе библиотеки [Fresco]
 *
 * @author sa.nikitin
 */
object FrescoPlugin : BasePlugin<FrescoPluginCustomizationOptions>() {

    private lateinit var apiServiceProvider: FeatureProvider<ApiService.Provider>
    private var sessionIdProvider: FeatureProvider<SessionIdProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ApiService.Provider::class.java) { apiServiceProvider = it }
            .apply {
                if (customizationOptions.authRequired) {
                    require(SessionIdProvider::class.java) { sessionIdProvider = it }
                }
            }
            .build()
    }

    override val customizationOptions: FrescoPluginCustomizationOptions = FrescoPluginCustomizationOptions()

    override fun doAfterInitialize() {
        initializeFresco(
            application = application,
            nativeCodeEnabled = customizationOptions.isNativeCodeEnabled,
            networkFetcherProvider = { _, _ ->
                OkHttpNetworkFetcher(
                    apiServiceProvider.get().apiService().frescoOkHttpClient
                        .apply {
                            if (customizationOptions.authRequired) {
                                val sessionIdProvider: SessionIdProvider? = sessionIdProvider?.get()
                                if (sessionIdProvider != null) {
                                    addInterceptor(CookieInterceptor(sessionIdProvider))
                                } else {
                                    illegalState { "SessionIdProvider is null, but required" }
                                }
                            }
                        }
                        .build()
                )
            },
            cacheDirProvider = customizationOptions.cacheDirProvider,
            cacheSize = customizationOptions.cacheSizeInMB,
            cacheKeyFactory = customizationOptions.cacheKeyFactory,
            postInitializer = null
        )
    }
}