package ru.tensor.sbis.media

import ru.tensor.sbis.attachments.loading.decl.presentation.DownloadFragmentFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.media.contract.MediaDependency
import ru.tensor.sbis.media.contract.MediaFeature
import ru.tensor.sbis.media.contract.MediaFeatureImpl
import ru.tensor.sbis.media.di.DaggerMediaComponent
import ru.tensor.sbis.media.di.MediaComponent
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.storage.contract.ExternalStorageProvider
import ru.tensor.sbis.storage.contract.InternalStorageProvider
import ru.tensor.sbis.toolbox_decl.media.VideoPlayer
import ru.tensor.sbis.verification_decl.login.LoginInterface

object MediaPlugin : BasePlugin<Unit>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var internalStorageProvider: FeatureProvider<InternalStorageProvider>
    private lateinit var apiServiceProvider: FeatureProvider<ApiService.Provider>
    private lateinit var downloadFragmentFactoryProvider: FeatureProvider<DownloadFragmentFactory>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    internal lateinit var externalStorageProvider: FeatureProvider<ExternalStorageProvider>

    private val mediaFeature: MediaFeature by lazy { MediaFeatureImpl() }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(VideoPlayer::class.java) { mediaFeature },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .require(ApiService.Provider::class.java) { apiServiceProvider = it }
        .require(DownloadFragmentFactory::class.java) { downloadFragmentFactoryProvider = it }
        .require(InternalStorageProvider::class.java) { internalStorageProvider = it }
        .require(ExternalStorageProvider::class.java) { externalStorageProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    internal val mediaComponent: MediaComponent by lazy {
        val dependency = object : MediaDependency,
                                  LoginInterface.Provider by loginInterfaceProvider.get(),
                                  InternalStorageProvider by internalStorageProvider.get(),
                                  ApiService.Provider by apiServiceProvider.get(),
                                  DownloadFragmentFactory by downloadFragmentFactoryProvider.get() {}

        DaggerMediaComponent.builder()
            .commonSingletonComponent(commonSingletonComponentProvider.get())
            .dependency(dependency)
            .build()
    }
}