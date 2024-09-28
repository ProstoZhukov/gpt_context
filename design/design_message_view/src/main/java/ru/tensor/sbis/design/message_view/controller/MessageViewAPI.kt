package ru.tensor.sbis.design.message_view.controller

import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.listener.MessageViewListenerChanges
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * API компонента [MessageView].
 *
 * @author dv.baranov
 */
interface MessageViewAPI {

    /** ViewData для отображения нужной ячейки. */
    var viewData: MessageViewData?

    /** Поведение компонента ячейка-облако CloudView для цитирования по свайпу. */
    val swipeToQuoteBehavior: MessageSwipeToQuoteBehavior

    /** Установить вью пул компонента. */
    fun setMessageViewPool(viewPool: MessageViewPool)

    /** Изменить слушатели. */
    fun changeEventListeners(changes: MessageViewListenerChanges)

    /**
     * Обновить view с датой [formattedDateTime].
     */
    fun setFormattedDateTime(formattedDateTime: FormattedDateTime)

    /**
     * Обновить view с состоянием доставки [sendingState].
     */
    fun updateSendingState(sendingState: SendingState)

    /**
     * Сменить прогресс (крутилку) отклонения подписи / доступа к файлу.
     */
    fun changeRejectProgress(show: Boolean)

    /**
     * Сменить прогресс (крутилку) предоставления доступа к файлу.
     */
    fun changeAcceptProgress(show: Boolean)

    /** @SelfDocumented */
    fun recycleViews()
}