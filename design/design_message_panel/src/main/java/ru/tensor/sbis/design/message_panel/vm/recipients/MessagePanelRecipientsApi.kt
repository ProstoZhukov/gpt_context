package ru.tensor.sbis.design.message_panel.vm.recipients

import android.content.Context
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientService
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import java.util.*

/**
 * Публичный API для управления адресатами панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelRecipientsApi {

    val recipients: StateFlow<RecipientsViewData>

    val recipientsHint: StateFlow<String>

    val recipientsAllChosenText: StateFlow<String>

    val recipientsVisible: StateFlow<Boolean>

    fun setRecipients(newRecipients: List<UUID>, isSelectedByUser: Boolean)

    /**
     * Принимает [context] т.к. реализация [RecipientService.launchSelection] может открывать
     * новую activity для выбора адресатов. Экземпляр [context] не харнится, доставляется
     * непосредственно в метод [RecipientService.launchSelection]
     */
    fun onRecipientSelectionClicked(context: Context)

    fun onRecipientsClearClicked()

    fun changeRecipientsVisibility(isVisible: Boolean)

    fun setRecipientsHint(hint: String)

    fun setRecipientsAllChosenText(text: String)
}