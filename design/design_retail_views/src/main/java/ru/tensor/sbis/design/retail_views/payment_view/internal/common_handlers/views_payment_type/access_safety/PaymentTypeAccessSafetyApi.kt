package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "тип оплаты". */
interface PaymentTypeAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] кнопки "Тип оплаты". */
    fun setPaymentTypeButtonVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для кнопки "Тип оплаты". */
    fun setPaymentTypeButtonInvisible(isInvisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "Тип оплаты". */
    fun setPaymentTypeButtonEnabled(isEnabled: Boolean)
    /*# endregion */
}