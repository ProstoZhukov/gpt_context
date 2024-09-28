package ru.tensor.sbis.message_panel.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.message_panel.MessagePanelPlugin
import ru.tensor.sbis.message_panel.contract.MessagePanelDependency
import ru.tensor.sbis.message_panel.di.attachmnets.MessagePanelAttachmentsComponent
import ru.tensor.sbis.message_panel.di.recipients.MessagePanelRecipientsComponent
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper

/**
 * @author Subbotenko Dmitry
 */
@PerApp
@Component(
        dependencies = [
            MessagePanelDependency::class
        ],
        modules = [
            SubscriptionManagerDIModule::class
        ]
)
interface MessagePanelComponent {

    val dependency: MessagePanelDependency
    val recipientSelectionProvider: RecipientSelectionProvider?
    val recipientSelectionMenuProvider: RecipientSelectionMenuProvider?
    val attachmentsComponentFactory: MessagePanelAttachmentsComponent.Factory
    val recipientsComponent: MessagePanelRecipientsComponent

    fun getMessageController(): DependencyProvider<MessageController>
    fun getResourceProvider(): ResourceProvider
    fun getSubscriptionManager(): SubscriptionManager
    fun getAttachmentController(): DependencyProvider<Attachment>

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun appContext(dependency: Context): Builder

        @BindsInstance
        fun attachmentController(dependency: DependencyProvider<Attachment>): Builder

        @BindsInstance
        fun messageController(dependency: DependencyProvider<MessageController>): Builder

        @BindsInstance
        fun recipientsController(dependency: DependencyProvider<RecipientsController>?): Builder

        @BindsInstance
        fun employeeProfileController(dependency: DependencyProvider<EmployeeProfileControllerWrapper>?): Builder

        @BindsInstance
        fun employeesController(dependency: DependencyProvider<EmployeesControllerWrapper>?): Builder

        @BindsInstance
        fun resourceProvider(dependency: ResourceProvider): Builder

        fun dependency(dependency: MessagePanelDependency): Builder

        fun build(): MessagePanelComponent
    }
}

object MessagePanelComponentProvider {
    operator fun get(context: Context): MessagePanelComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return MessagePanelPlugin.messagePanelComponent
    }
}