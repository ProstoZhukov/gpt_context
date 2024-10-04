package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "информация о долге". */
interface CreditInfoAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установить видимость [isVisible] для текста суммы долга. */
    fun setDebtTextVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для текста суммы долга. */
    fun setDebtTextInvisible(isInvisible: Boolean)
    /*# endregion */
}