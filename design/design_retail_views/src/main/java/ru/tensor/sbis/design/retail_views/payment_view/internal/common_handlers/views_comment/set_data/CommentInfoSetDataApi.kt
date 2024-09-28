package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data

/** Обобщение API для установки данных в блок "комментарий к оплате". */
interface CommentInfoSetDataApi {
    /** Установка значения [commentText] комментария. */
    fun setCommentTextValue(commentText: String?)
}