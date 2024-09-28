package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data

import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.toolbox_decl.share.ShareData

/**
 * Данные для отправки сообщения в разделе шаринга в контакты.
 *
 * @property shareData данные, которыми делится пользователь.
 * @property comment комментарий введенный пользователем.
 * @property recipients список получателей сообщения.
 *
 * @author vv.chekurda
 */
internal class SendContactsShareData(
    val shareData: ShareData,
    val comment: String,
    val recipients: List<SelectionItem>
)