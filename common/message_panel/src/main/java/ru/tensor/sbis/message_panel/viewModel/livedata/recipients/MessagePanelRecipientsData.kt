package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.Observable
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import java.util.*

/**
 * Модель данных получателей
 *
 * @author vv.chekurda
 * @since 7/16/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelRecipientsData {
    val recipientsFeatureEnabled: Observable<Boolean>
    val recipients: Observable<List<RecipientItem>>
    val isRecipientsHintEnabled: Observable<Boolean>
    val requireCheckAllMembers: Observable<Boolean>
    val recipientsUuidList: List<UUID>
    val requireRecipients: Boolean

    /**
     * Отметка о том, что получатели выбраны пользователем (не поставлены программно)
     */
    val recipientsSelected: Observable<Boolean>

    fun onRecipientButtonClick()
    fun onRecipientClearButtonClick()
    fun setRecipients(recipientsList: List<RecipientItem>, isUserSelected: Boolean = false)
    fun setRecipientsRequired(required: Boolean)
    fun setRecipientsSelected(isSelected: Boolean)
    fun isRecipientsSelected(): Boolean
    fun setRecipientsFeatureEnabled(enabled: Boolean)
}