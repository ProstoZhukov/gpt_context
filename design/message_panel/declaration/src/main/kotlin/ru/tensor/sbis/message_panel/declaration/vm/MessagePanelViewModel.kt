package ru.tensor.sbis.message_panel.declaration.vm

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData

/**
 * TODO: 11/11/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
interface MessagePanelViewModel {

    val newDialogMode: LiveData<Boolean>
    val messageText: LiveData<String>
    val messageHint: LiveData<Int>

    val sendButtonActivated: LiveData<Boolean>
    val sendButtonVisibility: LiveData<Int>

    fun setNewDialogMode(newDialogMode: Boolean)
    fun setMessageText(text: String)
    fun setMessageHint(@StringRes hintRes: Int)

    fun setSendButtonVisible(visible: Boolean)
}