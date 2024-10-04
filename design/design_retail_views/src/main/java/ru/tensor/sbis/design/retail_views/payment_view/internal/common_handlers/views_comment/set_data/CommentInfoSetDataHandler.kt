package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi

/** Реализация объекта для установки данных в блок "комментарий к оплате". */
internal class CommentInfoSetDataHandler(
    private val safetyApi: CommentInfoAccessSafetyApi,
    private val viewAccessApi: CommentInfoAccessDangerousApi
) : CommentInfoSetDataApi {

    override fun setCommentTextValue(commentText: String?) {
        safetyApi.setCommentTextVisibility(isVisible = commentText.orEmpty().isNotEmpty())

        /*
            Древняя проблема Android связанная с использованием 'italic' стиля текста.
            https://stackoverflow.com/questions/10243374/textview-cutting-off-a-letter-in-android
         */
        val fixItalicTextCutSymbol = " "
        viewAccessApi.commentTextView.text = "${commentText.orEmpty()}$fixItalicTextCutSymbol"
    }
}