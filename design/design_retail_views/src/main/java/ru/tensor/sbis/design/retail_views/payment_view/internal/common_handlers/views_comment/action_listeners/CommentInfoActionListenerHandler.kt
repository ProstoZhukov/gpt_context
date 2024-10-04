package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами - "комментарий к оплате". */
internal class CommentInfoActionListenerHandler(
    private val viewAccessApi: CommentInfoAccessDangerousApi
) : CommentInfoActionListenerApi {

    override fun setCommentClickListener(action: () -> Unit) {
        viewAccessApi.commentButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke() }
    }
}