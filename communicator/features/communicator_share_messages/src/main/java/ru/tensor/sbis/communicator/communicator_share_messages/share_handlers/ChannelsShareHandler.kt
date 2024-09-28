package ru.tensor.sbis.communicator.communicator_share_messages.share_handlers

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.MessagesShareFragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuItem
import ru.tensor.sbis.communicator.design.R as RDesign

/**
 * Реализация обработчика для шаринга в каналы.
 *
 * @author vv.chekurda
 */
internal class ChannelsShareHandler : ShareHandler {

    override val menuItem: ShareMenuItem =
        ShareMenuItem(
            id = NavxId.CHATS.name.lowercase(),
            icon = SbisMobileIcon.Icon.smi_ClientChat,
            title = RDesign.string.communicator_share_target_title_channels,
            order = 201
        )

    override val analyticHandlerName: String = CHANNELS_SHARE_ANALYTIC_NAME

    override val navxIds: Set<String> = NavxId.CHATS.ids

    override fun isQuickShareSupported(quickShareKey: String): Boolean =
        quickShareKey.contains(menuItem.id)

    override fun getShareContent(shareData: ShareData, quickShareKey: String?): Fragment =
        MessagesShareFragment.newInstance(
            registryType = CommunicatorRegistryType.ChatsRegistry(),
            shareData = shareData,
            quickShareKey = quickShareKey
        )
}

private const val CHANNELS_SHARE_ANALYTIC_NAME = "channels"