package ru.tensor.sbis.communicator.communicator_share_messages.share_handlers

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.ContactsShareFragment
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuItem

/**
 * Реализация обработчика для шаринга недавним контактам в новый диалог.
 *
 * @author vv.chekurda
 */
internal class ContactsShareHandler : ShareHandler {

    override val menuItem: ShareMenuItem =
        ShareMenuItem(
            id = CONTACTS_SHARE_ID,
            icon = SbisMobileIcon.Icon.smi_menuContacts,
            title = R.string.communicator_share_target_title_contacts,
            order = 100
        )

    override val navxIds: Set<String> = NavxId.DIALOGS.ids

    override val analyticHandlerName: String = CONTACTS_SHARE_ANALYTIC_NAME

    override fun isQuickShareSupported(quickShareKey: String): Boolean =
        quickShareKey.contains(NavxId.DIALOGS.name.lowercase())

    override fun getShareContent(shareData: ShareData, quickShareKey: String?): Fragment =
        ContactsShareFragment.newInstance(shareData = shareData, quickShareKey = quickShareKey)
}

private const val CONTACTS_SHARE_ID = "contacts"
private const val CONTACTS_SHARE_ANALYTIC_NAME = "contacts"
