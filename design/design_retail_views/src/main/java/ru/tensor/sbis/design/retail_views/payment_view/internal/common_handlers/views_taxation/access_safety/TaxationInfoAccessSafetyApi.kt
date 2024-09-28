package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "блок СНО". */
interface TaxationInfoAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] всего блока "СНО". */
    fun setTaxationSystemInfoVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на блок "СНО". */
    fun setTaxationSystemInfoEnabled(isEnabled: Boolean)
    /*# endregion */
}