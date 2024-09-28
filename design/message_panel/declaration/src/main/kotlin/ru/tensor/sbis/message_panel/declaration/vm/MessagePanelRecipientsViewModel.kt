package ru.tensor.sbis.message_panel.declaration.vm

import androidx.lifecycle.LiveData
import ru.tensor.sbis.persons.IContactVM

/**
 * TODO: 11/13/2020 Добавить документацию
 *
 * @author ma.kolpakov
 */
interface MessagePanelRecipientsViewModel {

    val recipients: LiveData<List<IContactVM>>
    val requireRecipients: LiveData<Boolean>
    val recipientsSelected: LiveData<Boolean>

    val recipientsVisibility: LiveData<Int>
    val recipientsButton1Visibility: LiveData<Int>
    val recipientsButton2Visibility: LiveData<Boolean>

    fun setRecipients(recipientList: List<IContactVM>, isUserSelected: Boolean = false)
    fun setRecipientsRequired(required: Boolean)
    fun setRecipientsButtonVisible(visible: Boolean)
    fun setRecipientsSelected(selected: Boolean)

    fun onRecipientButtonClick()
    fun onRecipientClearButtonClick()
}