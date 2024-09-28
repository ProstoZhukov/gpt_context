package ru.tensor.sbis.network_native

import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.network_native.apiservice.contract.ApiServiceInitializer
import ru.tensor.sbis.network_native.httpclient.CookieManagerProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин инструментов предоставляющих функционал взаимодействия с "Сетью"
 *
 * @author ds.vershinin
 */
object NetworkPlugin : BasePlugin<Unit>() {

    val apiServiceProvider: ApiService.Provider by lazy { ApiService.Provider { apiService } }

    private val apiService: ApiService by lazy {
        ApiServiceInitializer.buildApiService(application, cookieManagerProvider.get().cookieManager())
    }

    private lateinit var cookieManagerProvider: FeatureProvider<CookieManagerProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ApiService.Provider::class.java) { apiServiceProvider },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CookieManagerProvider::class.java) { cookieManagerProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}