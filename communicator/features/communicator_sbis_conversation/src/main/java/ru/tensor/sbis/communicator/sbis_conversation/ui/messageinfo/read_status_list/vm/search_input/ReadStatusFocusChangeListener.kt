package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

/**
 * Слушатель изменения фокуса в списке статусов прочитанности
 *
 * @author vv.chekurda
 */
internal interface ReadStatusFocusChangeListener {

    /**
     * Смена состояния фокуса
     * @param hasFocus true, если есть фокус
     */
    fun onFocusChanged(hasFocus: Boolean)
}