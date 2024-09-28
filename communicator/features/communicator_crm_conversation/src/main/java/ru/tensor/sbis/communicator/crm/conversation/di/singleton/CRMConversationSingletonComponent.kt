package ru.tensor.sbis.communicator.crm.conversation.di.singleton

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationDependency
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Singleton DI компонент сабмодуля сообщений CRM.
 *
 * @author da.zhukov
 */
@Component(
    modules = [CRMConversationSingletonModule::class],
    dependencies = [CommunicatorCommonComponent::class]
)
@PerApp
interface CRMConversationSingletonComponent : Feature {

    val context: Context
    val themedContext: SbisThemedContext
    val dependency: CRMConversationDependency

    val messageController: DependencyProvider<MessageController>
    val dialogController: DependencyProvider<DialogController>
    val chatController: DependencyProvider<ChatController>
    val messageCollectionStorageProvider: DependencyProvider<MessageCollectionStorageProvider>
    val clipboardManager: ClipboardManager
    val networkUtils: NetworkUtils
    val themeRepository: ThemeRepository
    val messagesPushManager: MessagesPushManager
    val detailAttachmentResourceHolder: DetailAttachmentResourcesHolder
    val dateTimeWithTodayFormatter: ListDateFormatter.DateTimeWithToday
    val dateTimeWithTodayCellsWithTimeFormatter: ListDateFormatter.DateTimeWithTodayCellsWithTime
    val dateTimeWithTodayStandardFormatter: ListDateFormatter.DateTimeWithTodayStandard
    val appLifecycleTracker: AppLifecycleTracker
    val audioWaveformHelper: AudioWaveformHelper
    val scrollHelper: ScrollHelper
    val localFeatureToggleService: LocalFeatureToggleService

    interface Holder {
        val crmConversationSingletonComponent: CRMConversationSingletonComponent
    }

    @Component.Builder
    interface Builder {
        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder
        @BindsInstance fun dependency(dependency: CRMConversationDependency): Builder
        fun build(): CRMConversationSingletonComponent
    }

    class Initializer(private val dependency: CRMConversationDependency) {

        fun init(communicatorCommonComponent: CommunicatorCommonComponent): CRMConversationSingletonComponent =
            DaggerCRMConversationSingletonComponent.builder()
                .communicatorCommonComponent(communicatorCommonComponent)
                .dependency(dependency)
                .build()
    }
}