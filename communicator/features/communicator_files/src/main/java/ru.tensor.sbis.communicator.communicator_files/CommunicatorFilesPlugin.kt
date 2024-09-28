package ru.tensor.sbis.communicator.communicator_files

import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.communicator_files.contract.CommunicatorFilesFeatureImpl
import ru.tensor.sbis.communicator.declaration.CommunicatorFilesFragmentFactory
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин файлов переписки.
 *
 * @author da.zhukov
 */
object CommunicatorFilesPlugin : BasePlugin<Any>() {

    private val filesFeature by lazy(::CommunicatorFilesFeatureImpl)

    internal lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    internal lateinit var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>
    internal lateinit var addAttachmentsUseCaseProvider: FeatureProvider<AddAttachmentsUseCase>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorFilesFragmentFactory::class.java) { filesFeature },
    )
    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
            .require(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
            .require(AddAttachmentsUseCase::class.java) { addAttachmentsUseCaseProvider = it }
            .build()
    }

    override val customizationOptions: Unit = Unit
}