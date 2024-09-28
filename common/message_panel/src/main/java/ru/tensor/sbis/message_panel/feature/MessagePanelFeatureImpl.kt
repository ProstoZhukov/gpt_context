package ru.tensor.sbis.message_panel.feature

import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.message_panel.attachments.viewer.DefaultViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.decl.AttachmentControllerProvider
import ru.tensor.sbis.message_panel.decl.MessageControllerProvider
import ru.tensor.sbis.message_panel.decl.RecipientsControllerProvider

/**
 * Реализация фичи панели сообщений
 */
internal class MessagePanelFeatureImpl : MessagePanelFeature,
    AttachmentControllerProvider,
    MessageControllerProvider,
    RecipientsControllerProvider {

    override fun viewerSliderArgsFactory(): ViewerSliderArgsFactory {
        return DefaultViewerSliderArgsFactory
    }

    override fun getAttachmentController(): DependencyProvider<Attachment> =
        DependencyProvider.create { Attachment.instance() }


    override fun getMessageController(): DependencyProvider<MessageController> =
        DependencyProvider.create { MessageController.instance() }

    override fun getRecipientsController(): DependencyProvider<RecipientsController> =
        DependencyProvider.create { RecipientsController.instance() }
}