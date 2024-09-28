package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "блок скидок". */
interface DiscountViewsAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] кнопки "Бонусы". */
    fun setBonusButtonVisibility(isVisible: Boolean)

    /** Установка видимости [isVisible] кнопки "Скидка". */
    fun setDiscountButtonVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "Бонусы". */
    fun setBonusButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Скидка". */
    fun setDiscountButtonEnabled(isEnabled: Boolean)
    /*# endregion */
}