package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "кнопки оплаты". */
interface PaymentButtonsAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установить видимость [isVisible] для кнопки "Оплата". */
    fun setCheckButtonVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для кнопки "Оплата". */
    fun setCheckButtonInvisible(isInvisible: Boolean)

    /** Установить видимость [isVisible] для кнопки "Оплата" с вводом оплачиваемого/вносимого значения. */
    fun setCheckDoubleButtonVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для кнопки "Оплата" с вводом оплачиваемого/вносимого значения. */
    fun setCheckDoubleButtonInvisible(isInvisible: Boolean)

    /** Установить видимость [isVisible] для кнопки "Оплата картой" с вводом оплачиваемого/вносимого значения. */
    fun setCardPaymentButtonVisibility(isVisible: Boolean)

    /** Установить невидимость [isInvisible] для кнопки "Оплата картой" с вводом оплачиваемого/вносимого значения. */
    fun setCardPaymentButtonInvisible(isInvisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "Оплата". */
    fun setCheckButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Оплата" с вводом значения оплачиваемого долга. */
    fun setCheckDoubleButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Оплата картой" с вводом значения оплачиваемого долга. */
    fun setPayCardDoubleButtonEnabled(isEnabled: Boolean)
    /*# endregion */

    /*# region TooltipApi */
    /** Показать подсказку с ошибкой в поле "Оплата картой". */
    fun showCardPaymentInputErrorTooltip(errorText: String)
    /*# endregion */
}