package ru.tensor.sbis.design.message_panel

import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewMode
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsService
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftServiceConfig
import ru.tensor.sbis.design.message_panel.decl.message.MessageServiceConfig
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientServiceConfig
import ru.tensor.sbis.design.message_panel.di.DaggerMessagePanelComponent
import ru.tensor.sbis.design.message_panel.di.MessagePanelComponent
import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.CurrentAccount

/**
 * TODO: 16.06.2022 Добавить документацию со ссылкой на view класс
 *
 * @author ma.kolpakov
 */
object MessagePanelPlugin : BasePlugin<MessagePanelPlugin.CustomisationOptions>() {

    private lateinit var messageServiceConfigProvider: FeatureProvider<MessageServiceConfig<Any, Any>>
    private var draftServiceConfigProvider: FeatureProvider<MessageDraftServiceConfig<Any>>? = null
    private var recipientServiceConfigProvider: FeatureProvider<RecipientServiceConfig<IContactVM>>? = null
    private var attachmentsServiceProvider: FeatureProvider<AttachmentsService>? = null
    private var attachmentsMappingServiceProvider: FeatureProvider<AttachmentModelMapperFactory>? = null
    internal var filesPickerTabFeatureFeatureProvider: FeatureProvider<SbisFilesPickerFactory>? = null
    private var accountServiceProvider: FeatureProvider<CurrentAccount>? = null

    internal lateinit var component: MessagePanelComponent

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency = Dependency.Builder()
        .require(MessageServiceConfig::class.java) {
            @Suppress("UNCHECKED_CAST")
            messageServiceConfigProvider = it as FeatureProvider<MessageServiceConfig<Any, Any>>
        }
        .optional(RecipientServiceConfig::class.java) {
            @Suppress("UNCHECKED_CAST")
            recipientServiceConfigProvider = it as FeatureProvider<RecipientServiceConfig<IContactVM>>
        }
        .optional(MessageDraftServiceConfig::class.java) {
            @Suppress("UNCHECKED_CAST")
            draftServiceConfigProvider = it as FeatureProvider<MessageDraftServiceConfig<Any>>
        }
        .optional(AttachmentsService::class.java) {
            attachmentsServiceProvider = it
        }
        .optional(AttachmentModelMapperFactory::class.java) {
            attachmentsMappingServiceProvider = it
        }
        .optional(SbisFilesPickerFactory::class.java) {
            filesPickerTabFeatureFeatureProvider = it
        }
        .optional(CurrentAccount::class.java) {
            accountServiceProvider = it
        }
        .build()

    override val customizationOptions = CustomisationOptions()

    class CustomisationOptions

    override fun initialize() {
        super.initialize()

        val messageServiceConfig = messageServiceConfigProvider.get()
        val recipientConfig = recipientServiceConfigProvider?.get() ?: TODO("Пока обязательно")
        val draftServiceConfig = draftServiceConfigProvider?.get() ?: TODO("Пока обязательно")
        //region Зависимости для работы с вложениями (нужни все или ни одного)
        val attachmentsService = attachmentsServiceProvider?.get() ?: TODO("Пока обязательно")
        val attachmentsMappingService = attachmentsMappingServiceProvider?.get() ?: TODO("Пока обязательно")
        val accountService = accountServiceProvider?.get() ?: TODO("Пока обязательно")
        //endregion
        component = DaggerMessagePanelComponent.builder()
            .bindAppContext(application)
            .messageService(messageServiceConfig.service)
            .messageServiceHelper(messageServiceConfig.serviceHelper)
            .recipientService(recipientConfig.service)
            .recipientServiceHelper(recipientConfig.serviceHelper)
            .draftService(draftServiceConfig.service)
            .draftServiceHelper(draftServiceConfig.serviceHelper)
            .attachmentsService(attachmentsService)
            .attachmentsMappingService(
                attachmentsMappingService.createAttachmentRegisterModelMapper(
                    AttachmentsViewMode.MESSAGE
                )
            )
            .accountService(accountService)
            .build()
    }
}
