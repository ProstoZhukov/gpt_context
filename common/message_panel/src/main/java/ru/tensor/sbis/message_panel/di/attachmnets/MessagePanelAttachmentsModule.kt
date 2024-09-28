package ru.tensor.sbis.message_panel.di.attachmnets

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.attachments.generated.AttachmentController
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.message_panel.contract.MessagePanelDependency
import ru.tensor.sbis.message_panel.interactor.attachments.DefaultMessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.attachments.model.AttachmentCatalogParams
import javax.inject.Named

internal const val ATTACHMENTS_CATALOG_DATA = "ATTACHMENTS_CATALOG_DATA"

/**
 * @author vv.chekurda
 */
@Module
internal class MessagePanelAttachmentsModule {

    @Provides
    fun provideDefaultAttachmentsInteractor(
        attachmentsController: DependencyProvider<Attachment>,
        singleAttachmentController: DependencyProvider<AttachmentController>,
        messagePanelDependency: MessagePanelDependency,
        @Named(ATTACHMENTS_CATALOG_DATA) catalogData: AttachmentCatalogParams
    ) : MessagePanelAttachmentsInteractor =
        DefaultMessagePanelAttachmentsInteractor(
            attachmentsController,
            singleAttachmentController,
            messagePanelDependency,
            messagePanelDependency,
            catalogData
        )

    @Provides
    fun provideSingleAttachmentController(): DependencyProvider<AttachmentController> =
        DependencyProvider.create { AttachmentController.instance() }
}