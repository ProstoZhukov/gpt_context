package ru.tensor.sbis.communicator.sbis_conversation.di.singleton

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.DialogDocumentController
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationListCommand
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPoolController
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_view.utils.MessageDecoratedLinkOpener
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.controller.person.PersonControllerWrapper
import ru.tensor.sbis.profiles.generated.PersonController

/**
 * Di singleton компонент модуля сообщений сбис
 *
 * @author vv.chekurda
 */
@Component(
    modules = [CommunicatorSbisConversationSingletonModule::class],
    dependencies = [CommunicatorCommonComponent::class]
)
@PerApp
internal interface CommunicatorSbisConversationSingletonComponent : Feature {

    val networkUtils: NetworkUtils
    val clipboardManager: ClipboardManager
    val messagesPushManager: MessagesPushManager
    val themeRepository: ThemeRepository
    val dialogControllerDependencyProvider: DependencyProvider<DialogController>
    val dialogDocumentControllerDependencyProvider: DependencyProvider<DialogDocumentController>
    val chatControllerDependencyProvider: DependencyProvider<ChatController>
    val messageController: DependencyProvider<MessageController>
    val messageCollectionStorageProvider: DependencyProvider<MessageCollectionStorageProvider>
    val themeParticipantsControllerDependencyProvider: DependencyProvider<ThemeParticipantsController>
    val contactsControllerWrapper: ContactsControllerWrapper
    val employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>
    val personControllerProvider: DependencyProvider<PersonController>
    val personControllerWrapperProvider: DependencyProvider<PersonControllerWrapper>
    val context: Context
    val themedContext: SbisThemedContext
    val dateTimeWithTodayShortFormatter: ListDateFormatter.DateTimeWithTodayShort
    val conversationToolbarEventManager: ConversationToolbarEventManager
    val attachmentController: DependencyProvider<Attachment>
    val detailAttachmentResourceHolder: DetailAttachmentResourcesHolder
    val conversationEventsPublisher: ConversationEventsPublisher
    val conversationPrefetchManager: ConversationPrefetchManager
    val conversationViewPoolController: ConversationViewPoolController
    val messageDecoratedLinkOpener: MessageDecoratedLinkOpener?
    val appLifecycleTracker: AppLifecycleTracker
    val communicatorActivityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
    val conversationListCommand: ConversationListCommand
    val avatarPrefetchHelper: PersonAvatarPrefetchHelper

    val communicatorCommonDependency: CommunicatorCommonDependency
    val dependency: CommunicatorSbisConversationDependency
    val directShareHelper: QuickShareHelper
    val localFeatureService: LocalFeatureToggleService
    val featureService: SbisFeatureService?

    interface Holder {
        val communicatorSbisConversationSingletonComponent: CommunicatorSbisConversationSingletonComponent
    }

    @Component.Builder
    interface Builder {
        @BindsInstance fun dependency(dependency: CommunicatorSbisConversationDependency): Builder
        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder
        fun build(): CommunicatorSbisConversationSingletonComponent
    }

    class Initializer(private val dependency: CommunicatorSbisConversationDependency) {

        fun init(communicatorCommonComponent: CommunicatorCommonComponent): CommunicatorSbisConversationSingletonComponent =
            DaggerCommunicatorSbisConversationSingletonComponent.builder()
                .communicatorCommonComponent(communicatorCommonComponent)
                .dependency(dependency)
                .build()
    }
}