package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

/**
 * Слушатель выбранного пункта действия с сообщением (редактировать/удалить и тд).
 *
 * @author vv.chekurda
 */
interface MessageSelectionItemListener {

    /**
     * Обработать клик на выбранное действие с сообщением.
     *
     * @param actionOrder порядковый номер действия.
     */
    fun onMessageActionClick(actionOrder: Int)
}