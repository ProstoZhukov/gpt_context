package ru.tensor.sbis.design.cloud_view

import ru.tensor.sbis.attachments.ui.utils.refresh.VisibleAttachmentsInteractor
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.design.cloud_view.content.attachments.AttachmentClickListener
import ru.tensor.sbis.design.cloud_view.content.attachments.DefaultCloudViewAttachmentClickListener
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин компонента ячейка-облако.
 *
 * @author vv.chekurda
 */
object CloudViewPlugin : BasePlugin<Unit>() {

    private var visibleAttachmentsInteractorFeature: FeatureProvider<VisibleAttachmentsInteractor>? = null
    private var complainServiceProviderFeature: FeatureProvider<ComplainService.Provider>? = null
    private var viewerSliderIntentFactoryFeature: FeatureProvider<ViewerSliderIntentFactory>? = null

    internal val visibleAttachmentsInteractor: VisibleAttachmentsInteractor? by lazy {
        visibleAttachmentsInteractorFeature?.get()
    }

    internal val defaultAttachmentClickListener: AttachmentClickListener? by lazy {
        DefaultCloudViewAttachmentClickListener(viewerSliderIntentFactoryFeature?.get())
    }

    override val dependency: Dependency
        get() = Dependency.Builder()
            .optional(VisibleAttachmentsInteractor::class.java) { visibleAttachmentsInteractorFeature = it }
            .optional(ComplainService.Provider::class.java) { complainServiceProviderFeature = it }
            .optional(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryFeature = it }
            .build()

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val customizationOptions = Unit
}