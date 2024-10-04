package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners

/** Обобщение Api для обработки действий пользователя с View элементами - "комментарий к оплате". */
interface CommentInfoActionListenerApi {
    /** Установить действие [action] по нажатию на кнопку "Комментарий". */
    fun setCommentClickListener(action: () -> Unit)
}