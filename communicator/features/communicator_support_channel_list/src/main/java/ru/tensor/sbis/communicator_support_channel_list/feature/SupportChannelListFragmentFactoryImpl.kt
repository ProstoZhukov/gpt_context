package ru.tensor.sbis.communicator_support_channel_list.feature

import android.content.Intent
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.communicator.push.SupportClientConversationCategory
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.data.SupportUnreadCounterProvider
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListDetailFragment
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListHostFragment
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.accessibility.RegistryAccessibilityService
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenSupportConversationDeepLinkAction
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.settings_screen_common.content.CounterInitializer
import ru.tensor.sbis.settings_screen_common.content.button.Button
import ru.tensor.sbis.settings_screen_common.content.button.asString
import ru.tensor.sbis.settings_screen_common.content.common.ShowUniversalFragment
import ru.tensor.sbis.settings_screen_decl.Item
import java.util.UUID

/**
 * Реализация CommunicatorSupportChatListFragmentFactory
 * @see SupportChannelListFragmentFactory
 */
internal class SupportChannelListFragmentFactoryImpl(
    private val supportUnreadCounterProvider: SupportUnreadCounterProvider,
    private val accessibilityService: RegistryAccessibilityService,
    private val mainActivityProvider: MainActivityProvider,
    private val crmConversationProvider: CRMConversationProvider
) : SupportChannelListFragmentFactory {

    override fun createSupportChatListFragmentSettingsItem(): Item = Button(
        titleRes = R.string.communicator_support_channel_list_syby_support_title,
        leftIcon = SbisMobileIcon.Icon.smi_Support.asString(),
        action = ShowUniversalFragment { _, _ ->
            SupportChannelListDetailFragment.newInstance(
                SupportComponentConfig.SabySupport,
                null,
                null
            )
        },
        initialize = CounterInitializer(supportUnreadCounterProvider)
    )

    override fun createClientsSupportChatListFragmentSettingsItem(): Item = Button(
        titleRes = R.string.communicator_support_channel_list_syby_support_title,
        leftIcon = SbisMobileIcon.Icon.smi_Support.asString(),
        action = ShowUniversalFragment { _, _ ->
            SupportChannelListHostFragment.newInstance(SupportComponentConfig.SabySupport)
        },
        initialize = CounterInitializer(supportUnreadCounterProvider)
    )

    override fun getOpenSupportConversationIntent(
        dialogUuid: UUID,
        conversationTitle: String?
    ): Intent {
        val action = OpenSupportConversationDeepLinkAction(dialogUuid, conversationTitle)
        val intent = mainActivityProvider.getMainActivityIntent().apply {
            putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, action)
            putExtra(
                IntentAction.Extra.PUSH_CONTENT_CATEGORY,
                SupportClientConversationCategory()
            )
        }
        return intent
    }

    override fun getOpenSabySupportConversationIntent(
        dialogUuid: UUID,
        conversationTitle: String?
    ): Intent {
        return crmConversationProvider.getCRMConversationActivityIntent(
            CRMConsultationOpenParams(
                relevantMessageUuid = null,
                isCompleted = false,
                needBackButton = true,
                crmConsultationCase = CRMConsultationCase.Client(dialogUuid)
            )
        )
    }

    override suspend fun isSupportAvailable(): Boolean =
        accessibilityService.isSupportAvailable()
}