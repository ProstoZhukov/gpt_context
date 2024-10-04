package ru.tensor.sbis.message_panel.declaration.vm

import androidx.lifecycle.LiveData

/**
 * TODO: 11/13/2020 Добавить документацию
 *
 * @author ma.kolpakov
 */
interface MessagePanelQuoteViewModel {

    val quoteTitle: LiveData<String>
    val quoteText: LiveData<String>

    val quotePanelVisible: LiveData<Int>

    fun setQuoteText(title: String, text: String)

    fun setQuoteVisible(visible: Boolean)
}