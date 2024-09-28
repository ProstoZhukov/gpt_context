package ru.tensor.sbis.communicator.sbis_conversation.di.singleton

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ConversationDataMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.DocumentMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractorImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationListCommand
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationPrefetchManagerImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationRepository
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.MessageListMapper
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPoolController
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_view.utils.MessageDecoratedLinkOpener
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.controller.person.PersonControllerWrapper
import ru.tensor.sbis.profiles.generated.PersonController

/**
 * Di модуль singleton компонента переписки сбис
 *
 * @author vv.chekurda
 */
@Module
class CommunicatorSbisConversationSingletonModule {

    @Provides
    fun provideConversationToolbarEventManager(dependency: CommunicatorSbisConversationDependency): ConversationToolbarEventManager =
        dependency.getConversationToolbarEventManager()

    @Provides
    @PerApp
    fun provideEmployeeProfileControllerWrapper(dependency: CommunicatorSbisConversationDependency): DependencyProvider<EmployeeProfileControllerWrapper> =
        dependency.employeeProfileControllerWrapper

    @Provides
    @PerApp
    fun providePersonController(): DependencyProvider<PersonController> =
        DependencyProvider.create(PersonController::instance)

    @Provides
    @PerApp
    fun providePersonControllerWrapper(
        dependency: CommunicatorSbisConversationDependency
    ): DependencyProvider<PersonControllerWrapper> =
        dependency.personControllerWrapper

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationPrefetchManagerImpl(
        conversationListCommand: ConversationListCommand,
        messageMapper: MessageMapper,
        conversationDataInteractor: ConversationDataInteractor
    ): ConversationPrefetchManager =
        ConversationPrefetchManagerImpl(
            conversationListCommand,
            messageMapper,
            conversationDataInteractor
        )

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationListCommand(
        conversationRepository: ConversationRepository,
        themeRepository: ThemeRepository,
        messageListMapper: MessageListMapper
    ): ConversationListCommand =
        ConversationListCommand(conversationRepository, themeRepository, messageListMapper)

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationDataInteractor(
        dialogControllerProvider: DependencyProvider<DialogController>,
        chatControllerProvider: DependencyProvider<ChatController>,
        conversationDataMapper: ConversationDataMapper,
        avatarPrefetchHelper: PersonAvatarPrefetchHelper
    ): ConversationDataInteractor =
        ConversationDataInteractorImpl(dialogControllerProvider, chatControllerProvider, conversationDataMapper, avatarPrefetchHelper)

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationRepository(
        messageController: DependencyProvider<MessageController>,
        messageControllerBinaryMapper: MessageControllerBinaryMapper,
        activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): ConversationRepository =
        ConversationRepository(messageController, messageControllerBinaryMapper, activityStatusSubscriptionInitializer)

    @Provides
    @PerApp
    internal fun provideMessageControllerBinaryMapper(): MessageControllerBinaryMapper =
        MessageControllerBinaryMapper()

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideMessageMapper(
        context: SbisThemedContext
    ): MessageMapper = MessageMapper(context, false, false, optimizeConvert = true)

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideDocumentMapper(context: Context): DocumentMapper = DocumentMapper(context)

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationDataMapper(
        context: Context,
        documentMapper: DocumentMapper,
        messageMapper: MessageMapper,
        dateTimeFormatter: ListDateFormatter.DateTimeWithTodayShort
    ): ConversationDataMapper {
        return ConversationDataMapper(
            context,
            documentMapper,
            messageMapper,
            dateTimeFormatter,
            null
        )
    }

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideMessageListMapper(context: Context, messageMapper: MessageMapper): MessageListMapper =
        MessageListMapper(context, messageMapper)

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun providerConversationPoolController(): ConversationViewPoolController =
        ConversationViewPoolController()

    @Suppress("unused")
    @Provides
    @PerApp
    internal fun provideConversationDecoratedLinkOpener(): MessageDecoratedLinkOpener =
        MessageDecoratedLinkOpener()

    @Provides
    @PerApp
    internal fun provideLocalFeatureToggleService(
        context: Context
    ): LocalFeatureToggleService =
        LocalFeatureToggleService(context)

    @Provides
    @PerApp
    internal fun provideSbisFeatureService(
        dependency: CommunicatorSbisConversationDependency
    ): SbisFeatureService? =
        dependency.sbisFeatureServiceProvider?.sbisFeatureService
}