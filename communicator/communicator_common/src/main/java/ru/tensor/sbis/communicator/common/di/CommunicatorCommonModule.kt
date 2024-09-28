package ru.tensor.sbis.communicator.common.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.attachments.decl.action.AttachmentRequestAccessProvider
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewMode
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common_attachments.DefaultDetailAttachmentResourcesHolder
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin.personActivityStatusNotifierProvider
import ru.tensor.sbis.communicator.common.analytics.CommunicatorAnalyticsUtil
import ru.tensor.sbis.communicator.common.attachment_access.AttachmentRequestAccessProviderImpl
import ru.tensor.sbis.communicator.common.audio_player_view.MessageControllerWaveformDownscaleUtil
import ru.tensor.sbis.communicator.common.background_sync.MessagesBackgroundSyncManager
import ru.tensor.sbis.communicator.common.contacts.ContactsControllerWrapperImpl
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResultManager
import ru.tensor.sbis.communicator.common.message_panel.MessagesAudioWaveformHelper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelperImpl
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * DI модуль common communicator
 *
 * @author vv.chekurda
 */
@Suppress("unused")
@Module
class CommunicatorCommonModule {

    @CommunicatorCommonScope
    @Provides
    internal fun provideFeature(dependency: CommunicatorCommonDependency): CommunicatorCommonFeature =
        dependency as CommunicatorCommonFeature

    @CommunicatorCommonScope
    @Provides
    internal fun provideMessageController(): DependencyProvider<MessageController> =
        DependencyProvider.create { MessageController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideMessageCollectionStorageProvider(): DependencyProvider<MessageCollectionStorageProvider> =
        DependencyProvider.create { MessageCollectionStorageProvider.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideMessageControllerBinaryMapper(): MessageControllerBinaryMapper =
        MessageControllerBinaryMapper()

    @CommunicatorCommonScope
    @Provides
    internal fun provideDialogController(): DependencyProvider<DialogController> =
        DependencyProvider.create { DialogController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideThemeController(): DependencyProvider<ThemeController> =
        DependencyProvider.create { ThemeController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideDialogDocumentController(): DependencyProvider<DialogDocumentController> =
        DependencyProvider.create { DialogDocumentController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideChatController(): DependencyProvider<ChatController> =
        DependencyProvider.create { ChatController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideAttachmentController(): DependencyProvider<Attachment> =
        DependencyProvider.create { Attachment.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideThemeParticipantsController(): DependencyProvider<ThemeParticipantsController> =
        DependencyProvider.create { ThemeParticipantsController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun providerDialogAttachmentsController(): DependencyProvider<DialogAttachmentsController> =
        DependencyProvider.create(DialogAttachmentsController::instance)

    @CommunicatorCommonScope
    @Provides
    internal fun provideContactsController(): DependencyProvider<ContactsController> =
        DependencyProvider.create { ContactsController.instance() }

    @CommunicatorCommonScope
    @Provides
    internal fun provideThemeRepository(
        dependency: CommunicatorCommonDependency,
        themeController: DependencyProvider<ThemeController>,
        avatarPrefetchHelper: PersonAvatarPrefetchHelper
    ): ThemeRepository =
        dependency.getThemeRepository(themeController).also {
            it.avatarPrefetchHelper = avatarPrefetchHelper
        }

    @CommunicatorCommonScope
    @Provides
    internal fun providePersonAvatarPrefetchHelper(
        appContext: Context
    ): PersonAvatarPrefetchHelper =
        PersonAvatarPrefetchHelper(appContext)

    @CommunicatorCommonScope
    @Provides
    fun provideRecipientsController(): DependencyProvider<RecipientsController> =
        DependencyProvider.create(RecipientsController::instance)

    @CommunicatorCommonScope
    @Provides
    internal fun provideDialogSelectionResultManager(): MultiSelectionResultManager<DialogSelectionResult> =
        DialogSelectionResultManager()

    @CommunicatorCommonScope
    @Provides
    internal fun provideAttachmentResourcesHolder(context: Context): DetailAttachmentResourcesHolder =
        DefaultDetailAttachmentResourcesHolder(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideConversationMapper(
        context: SbisThemedContext,
        dependency: CommunicatorCommonDependency,
    ): ConversationMapper =
        ConversationMapper(context, dependency.createAttachmentRegisterModelMapper(AttachmentsViewMode.REGISTRY))

    @CommunicatorCommonScope
    @Provides
    internal fun provideDateWithMonth(): ListDateFormatter.DateWithMonth =
        ListDateFormatter.DateWithMonth()

    @CommunicatorCommonScope
    @Provides
    internal fun provideDateTimeWithTodayFormatter(context: Context): ListDateFormatter.DateTimeWithToday =
        ListDateFormatter.DateTimeWithToday(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideDateTimeWithTodayCellsWithTimeFormatter(context: Context): ListDateFormatter.DateTimeWithTodayCellsWithTime =
        ListDateFormatter.DateTimeWithTodayCellsWithTime(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideDateTimeWithTodayStandard(context: Context): ListDateFormatter.DateTimeWithTodayStandard =
        ListDateFormatter.DateTimeWithTodayStandard(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideTimeWithTodayAndDateElse(
        context: Context
    ): ListDateFormatter.DateTimeWithTodayShort =
        ListDateFormatter.DateTimeWithTodayShort(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideTimeForTodayAndDateElseForRegistry(
        context: Context
    ): ListDateFormatter.TimeForTodayAndShortDateElse =
        ListDateFormatter.TimeForTodayAndShortDateElse(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideConversationEventsPublisher(): ConversationEventsPublisher =
        ConversationEventsPublisher()

    @CommunicatorCommonScope
    @Provides
    internal fun provideMessagesBackgroundSyncManager(
        context: Context,
        appLifecycleTracker: AppLifecycleTracker,
        messageControllerProvider: DependencyProvider<MessageController>,
    ): MessagesBackgroundSyncManager =
        MessagesBackgroundSyncManager(context, appLifecycleTracker, messageControllerProvider)

    @CommunicatorCommonScope
    @Provides
    internal fun provideAttachmentRequestAccessProvider(controllerProvider: DependencyProvider<MessageController>): AttachmentRequestAccessProvider =
        AttachmentRequestAccessProviderImpl(controllerProvider)

    @CommunicatorCommonScope
    @Provides
    internal fun provideAudioWaveformHelper(
        context: Context,
        messageControllerProvider: DependencyProvider<MessageController>,
    ): AudioWaveformHelper =
        MessagesAudioWaveformHelper(context.cacheDir.path, messageControllerProvider)

    @CommunicatorCommonScope
    @Provides
    internal fun provideWaveformDownscaleUtil(): WaveformDownscaleUtil =
        MessageControllerWaveformDownscaleUtil()

    @CommunicatorCommonScope
    @Provides
    internal fun provideAnalyticsUtil(): AnalyticsUtil = CommunicatorAnalyticsUtil()

    @CommunicatorCommonScope
    @Provides
    internal fun provideCommunicatorActivityStatusSubscriptionInitializer(): CommunicatorActivityStatusSubscriptionInitializer =
        CommunicatorActivityStatusSubscriptionInitializer(personActivityStatusNotifierProvider.get())

    @CommunicatorCommonScope
    @Provides
    internal fun provideQuickShareHelper(context: Context): QuickShareHelper =
        QuickShareHelperImpl(context)

    @CommunicatorCommonScope
    @Provides
    internal fun provideContactsControllerWrapper(
        contactsControllerProvider: DependencyProvider<ContactsController>
    ): ContactsControllerWrapper =
        ContactsControllerWrapperImpl(contactsControllerProvider)
}
