package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView

/**
 * Интерфейс управления панели с получателями
 *
 * @author vv.chekurda
 * @since 7/17/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelRecipientsView {
    val recipientsVisibility: Observable<Boolean>
    /**
     * Данные получателей для отображения в панели с учётом надписи "Всем участникам" при передаче пустого списка
     */
    val recipientsViewData: Observable<RecipientsView.RecipientsViewData>
    val recipientSelectionScreen: Observable<RecipientSelectionConfig>
    val recipientSelectionMenu: Observable<RecipientSelectionMenuConfig>
    val recipientSelectionMenuVisibility: Observable<Boolean>

    fun setRecipientsPanelVisibility(isVisible: Boolean)
    fun forceHideRecipientsPanel(hide: Boolean)
    fun requestRecipientsSelection()
    fun requestRecipientSelectionMenu()
    fun changeRecipientSelectionMenuVisibility(isVisible: Boolean)
}