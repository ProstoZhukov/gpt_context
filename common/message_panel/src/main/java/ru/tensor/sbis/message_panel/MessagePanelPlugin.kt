package ru.tensor.sbis.message_panel

import android.app.Activity
import android.content.Context
import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.action.DeleteAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuProvider
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.message_panel.contract.MessagePanelDependency
import ru.tensor.sbis.message_panel.delegate.MessagePanelFilesPickerConfig
import ru.tensor.sbis.message_panel.di.DaggerMessagePanelComponent
import ru.tensor.sbis.message_panel.di.MessagePanelComponent
import ru.tensor.sbis.message_panel.feature.MessagePanelFeature
import ru.tensor.sbis.message_panel.feature.MessagePanelFeatureImpl
import ru.tensor.sbis.message_panel.helper.MessagePanelMentionFeature
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import ru.tensor.sbis.recorder.decl.RecorderViewDependency
import ru.tensor.sbis.recorder.decl.RecorderViewDependencyProvider
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин панели сообщений
 *
 * @author kv.martyshenko
 */
object MessagePanelPlugin : BasePlugin<MessagePanelPlugin.CustomizationOptions>() {

    lateinit var resourceProvider: FeatureProvider<ResourceProvider>
    private lateinit var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var attachmentModelMapperFactoryProvider: FeatureProvider<AttachmentModelMapperFactory>
    private lateinit var addAttachmentsUseCaseProvider: FeatureProvider<AddAttachmentsUseCase>
    private lateinit var deleteAttachmentsUseCaseProvider: FeatureProvider<DeleteAttachmentsUseCase>
    private lateinit var sbisFilesPickerFactoryProvider: FeatureProvider<SbisFilesPickerFactory>
    private var recipientSelectionFeatureProvider: FeatureProvider<RecipientSelectionProvider>? = null
    private var recipientSelectionMenuFeatureProvider: FeatureProvider<RecipientSelectionMenuProvider>? = null
    private var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>? = null
    private var employeesControllerWrapperProvider: FeatureProvider<EmployeesControllerWrapper.Provider>? = null
    private var recorderViewDependencyProvider: FeatureProvider<RecorderViewDependencyProvider>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var featureServiceProvider: FeatureProvider<SbisFeatureServiceProvider>? = null
    private val messagePanelFeature by lazy { MessagePanelFeatureImpl() }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(MessagePanelFeature::class.java) { messagePanelFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(ResourceProvider::class.java) { resourceProvider = it }
        .require(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
        .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .require(AttachmentModelMapperFactory::class.java) { attachmentModelMapperFactoryProvider = it }
        .require(AddAttachmentsUseCase::class.java) { addAttachmentsUseCaseProvider = it }
        .require(DeleteAttachmentsUseCase::class.java) { deleteAttachmentsUseCaseProvider = it }
        .require(SbisFilesPickerFactory::class.java) { sbisFilesPickerFactoryProvider = it }
        .optional(RecipientSelectionProvider::class.java) { recipientSelectionFeatureProvider = it }
        .optional(RecipientSelectionMenuProvider::class.java) { recipientSelectionMenuFeatureProvider = it }
        .optional(EmployeeProfileControllerWrapper.Provider::class.java) { employeeProfileControllerWrapperProvider = it }
        .optional(EmployeesControllerWrapper.Provider::class.java) { employeesControllerWrapperProvider = it }
        .optional(RecorderViewDependencyProvider::class.java) { recorderViewDependencyProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .optional(SbisFeatureServiceProvider::class.java) { featureServiceProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    internal val messagePanelComponent: MessagePanelComponent by lazy {
        val dependency = object : MessagePanelDependency,
            ViewerSliderIntentFactory by viewerSliderIntentFactoryProvider.get(),
            LoginInterface.Provider by loginInterfaceProvider.get(),
            AttachmentModelMapperFactory by attachmentModelMapperFactoryProvider.get(),
            AddAttachmentsUseCase by addAttachmentsUseCaseProvider.get(),
            DeleteAttachmentsUseCase by deleteAttachmentsUseCaseProvider.get(),
            SbisFilesPickerFactory by sbisFilesPickerFactoryProvider.get() {

            override val recipientSelectionProvider: RecipientSelectionProvider? =
                recipientSelectionFeatureProvider?.get()

            override val recipientSelectionMenuProvider: RecipientSelectionMenuProvider? =
                recipientSelectionMenuFeatureProvider?.get()

            override val analyticsUtilProvider: AnalyticsUtil.Provider? =
                analyticsUtilFeatureProvider?.get()

            override fun getRecorderViewDependency(
                context: Context,
                activity: Activity,
                swipeBackLayout: SwipeBackLayout?
            ): RecorderViewDependency? {
                return recorderViewDependencyProvider?.get()?.getRecorderViewDependency(context, activity, swipeBackLayout)
                    ?: super.getRecorderViewDependency(context, activity, swipeBackLayout)
            }
        }

        DaggerMessagePanelComponent.builder()
            .appContext(application)
            .resourceProvider(resourceProvider.get())
            .attachmentController(messagePanelFeature.getAttachmentController())
            .messageController(messagePanelFeature.getMessageController())
            .recipientsController(messagePanelFeature.getRecipientsController())
            .employeeProfileController(employeeProfileControllerWrapperProvider?.run { get().employeeProfileControllerWrapper })
            .employeesController(employeesControllerWrapperProvider?.get()?.getEmployeesControllerWrapper())
            .dependency(dependency)
            .build()
    }

    override fun doAfterInitialize() {
        super.doAfterInitialize()
        MessagePanelMentionFeature.init(
            LocalFeatureToggleService(application),
            featureServiceProvider?.get()?.sbisFeatureService
        )
    }

    /** Конфигурация плагина */
    class CustomizationOptions internal constructor() {

        /**
         * Конфигурация компонента выбора файлов.
         */
        var filesPickerConfig: MessagePanelFilesPickerConfig = MessagePanelFilesPickerConfig()
    }
}