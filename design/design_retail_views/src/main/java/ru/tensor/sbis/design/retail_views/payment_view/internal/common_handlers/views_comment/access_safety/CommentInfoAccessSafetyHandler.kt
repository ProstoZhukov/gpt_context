package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "комментарий к оплате". */
internal class CommentInfoAccessSafetyHandler(
    private val viewAccessApi: CommentInfoAccessDangerousApi
) : CommentInfoAccessSafetyApi {

    override fun setCommentTextVisibility(isVisible: Boolean) {
        viewAccessApi.commentTextView.isVisible = isVisible
    }

    override fun setCommentTextInvisible(isInvisible: Boolean) {
        viewAccessApi.commentTextView.isInvisible = isInvisible
    }

    override fun setCommentButtonVisibility(isVisible: Boolean) {
        viewAccessApi.commentButton.isVisible = isVisible
    }

    override fun setCommentButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.commentButton.isEnabled = isEnabled
    }
}