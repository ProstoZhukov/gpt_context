package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety

/** Обобщение API для безопасного изменения состояния View в шапке окна оплаты. */
interface ToolbarViewsAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] кнопки "Клиент". */
    fun setClientButtonVisibility(isVisible: Boolean)

    /** Установка видимости [isVisible] кнопки "Закрыть". */
    fun setCloseButtonVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "Клиент". */
    fun setClientButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Закрыть". */
    fun setCloseButtonEnabled(isEnabled: Boolean)
    /*# endregion */
}