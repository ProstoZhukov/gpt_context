package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "комментарий к оплате". */
interface CommentInfoAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] комментария. */
    fun setCommentTextVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для комментария. */
    fun setCommentTextInvisible(isInvisible: Boolean)

    /** Установка видимости [isVisible] кнопки добавления комментария. */
    fun setCommentButtonVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "Комментарий". */
    fun setCommentButtonEnabled(isEnabled: Boolean)
    /*# endregion */
}