package ru.tensor.sbis.communicator.common.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import ru.tensor.sbis.attachments.decl.action.AttachmentRequestAccessProvider
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin
import ru.tensor.sbis.communicator.common.background_sync.MessagesBackgroundSyncManager
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DialogAttachmentsController
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.DialogDocumentController
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * DI компонент common модуля communicator
 *
 * @author rv.krohalev
 */
@CommunicatorCommonScope
@Component(
    dependencies = [CommonSingletonComponent::class, CommunicatorCommonDependency::class],
    modules = [CommunicatorCommonModule::class]
)
interface CommunicatorCommonComponent : Feature {

    val feature: CommunicatorCommonFeature
    val context: Context
    val themedContext: SbisThemedContext
    val dependency: CommunicatorCommonDependency

    val messageController: DependencyProvider<MessageController>
    val storageProvider: DependencyProvider<MessageCollectionStorageProvider>
    val dialogController: DependencyProvider<DialogController>
    val dialogDocumentController: DependencyProvider<DialogDocumentController>
    val themeController: DependencyProvider<ThemeController>
    val attachmentController: DependencyProvider<Attachment>
    val chatController: DependencyProvider<ChatController>
    val recipientsController: DependencyProvider<RecipientsController>
    val themeParticipantsController: DependencyProvider<ThemeParticipantsController>
    val dialogAttachmentsController: DependencyProvider<DialogAttachmentsController>
    val contactsControllerWrapper: ContactsControllerWrapper
    val dialogSelectionResultManager: MultiSelectionResultManager<DialogSelectionResult>
    val dateTimeWithTodayFormatter: ListDateFormatter.DateTimeWithToday
    val dateTimeWithTodayCellsWithTimeFormatter: ListDateFormatter.DateTimeWithTodayCellsWithTime
    val dateWithMonthFormatter: ListDateFormatter.DateWithMonth
    val dateTimeWithTodayStandardFormatter: ListDateFormatter.DateTimeWithTodayStandard
    val dateTimeWithTodayShort: ListDateFormatter.DateTimeWithTodayShort
    val timeForTodayAndShortDateElse: ListDateFormatter.TimeForTodayAndShortDateElse
    val clipboardManager: ClipboardManager
    val networkUtils: NetworkUtils
    val messagesPushManager: MessagesPushManager
    val themeRepository: ThemeRepository
    val detailAttachmentResourceHolder: DetailAttachmentResourcesHolder
    val conversationMapper: ConversationMapper
    val scrollHelper: ScrollHelper
    val sharedPreferences: SharedPreferences
    val resourceProvider: ResourceProvider
    val conversationEventsPublisher: ConversationEventsPublisher
    val uriWrapper: UriWrapper
    val fileUriUtil: FileUriUtil
    val appRxBus: RxBus
    val messagesBackgroundSyncManager: MessagesBackgroundSyncManager
    val appLifecycleTracker: AppLifecycleTracker
    val attachmentRequestAccessProvider: AttachmentRequestAccessProvider
    val audioWaveformHelper: AudioWaveformHelper
    val waveformDownscaleUtil: WaveformDownscaleUtil
    val analyticsUtil: AnalyticsUtil
    val communicatorActivityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
    val directShareHelper: QuickShareHelper
    val avatarPrefetchHelper: PersonAvatarPrefetchHelper

    @Component.Builder
    interface Builder {
        fun commonSingletonComponent(commonSingletonComponent: CommonSingletonComponent): Builder
        fun dependency(dependency: CommunicatorCommonDependency): Builder
        fun build(): CommunicatorCommonComponent
    }

    class Initializer(private val dependency: CommunicatorCommonDependency) : BaseSingletonComponentInitializer<CommunicatorCommonComponent>() {

        override fun createComponent(commonSingletonComponent: CommonSingletonComponent): CommunicatorCommonComponent =
            DaggerCommunicatorCommonComponent.builder()
                .commonSingletonComponent(commonSingletonComponent)
                .dependency(dependency)
                .build()

        override fun initSingletons(singletonComponent: CommunicatorCommonComponent) {
            singletonComponent.messagesBackgroundSyncManager
        }
    }

    interface Holder {
        val communicatorCommonComponent: CommunicatorCommonComponent
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context): CommunicatorCommonComponent {
            return when(val app = context.applicationContext) {
                is Holder -> app.communicatorCommonComponent
                else -> CommunicatorCommonPlugin.communicatorCommonComponent
            }
        }
    }
}