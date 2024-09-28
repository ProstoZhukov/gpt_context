package ru.tensor.sbis.design.media_player

import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.design.media_player.contract.MediaPlayerDependency
import ru.tensor.sbis.design.media_player.contract.MediaPlayerFeatureImpl
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Плагин для компонента Медиа проигрыватель.
 *
 * @author rv.krohalev
 */
object MediaPlayerPlugin : BasePlugin<Unit>() {

    /**
     * Фича модуля медиа проигрывателя.
     * public для приложения DesignDemo
     */
    val feature: MediaPlayerFeature by lazy {
        MediaPlayerFeatureImpl(
            application.applicationContext,
            loginInterfaceProvider.get().loginInterface,
            apiServiceProvider.get().apiService()
        )
    }

    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var apiServiceProvider: FeatureProvider<ApiService.Provider>
    internal var callStateFeatureProvider: FeatureProvider<CallStateProvider>? = null

    internal var mediaPlayerDependency: MediaPlayerDependency? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(MediaPlayerFeature::class.java) { feature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .require(ApiService.Provider::class.java) { apiServiceProvider = it }
        .optional(CallStateProvider::class.java) { callStateFeatureProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        mediaPlayerDependency = object :
            MediaPlayerDependency,
            LoginInterface.Provider by loginInterfaceProvider.get(),
            ApiService.Provider by apiServiceProvider.get() {

            override val callStateProvider: CallStateProvider? =
                callStateFeatureProvider?.get()
        }
    }
}