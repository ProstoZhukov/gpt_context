package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Реализация объекта для прямого доступа к View элементам "комментарий к оплате". */
@DangerousApi
internal class CommentInfoAccessDangerousHandler(
    private val rootDelegateContainer: View
) : CommentInfoAccessDangerousApi {

    override val commentButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_comment_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "commentButton",
                viewResIdName = "R.id.retail_views_comment_button"
            )

    override val commentTextView: SbisTextView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_txt_comment)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "commentTextView",
                viewResIdName = "R.id.retail_views_txt_comment"
            )
}