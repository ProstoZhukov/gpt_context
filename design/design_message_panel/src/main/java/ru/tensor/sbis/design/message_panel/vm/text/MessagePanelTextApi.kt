package ru.tensor.sbis.design.message_panel.vm.text

import kotlinx.coroutines.flow.StateFlow

/**
 * Публичный API для работы с текстом панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelTextApi {

    val text: StateFlow<String>

    fun setText(newText: String)
}
