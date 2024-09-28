package ru.tensor.sbis.communicator.communicator_share_messages.share_handlers

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.MessagesShareFragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuItem

/**
 * Реализация обработчика для шаринга в каналы.
 *
 * @author vv.chekurda
 */
internal class DialogsShareHandler : ShareHandler {

    override val menuItem: ShareMenuItem =
        ShareMenuItem(
            id = DIALOGS_SHARE_ID,
            icon = SbisMobileIcon.Icon.smi_menuMessages,
            title = R.string.communicator_share_target_title_dialogs,
            order = 200
        )

    override val navxIds: Set<String> = NavxId.DIALOGS.ids

    override val analyticHandlerName: String = DIALOGS_SHARE_ANALYTIC_NAME

    override fun getShareContent(shareData: ShareData, quickShareKey: String?): Fragment =
        MessagesShareFragment.newInstance(
            registryType = CommunicatorRegistryType.DialogsRegistry(),
            shareData = shareData,
            quickShareKey = quickShareKey
        )
}

private const val DIALOGS_SHARE_ID = "dialogs"
private const val DIALOGS_SHARE_ANALYTIC_NAME = "dialogs"
