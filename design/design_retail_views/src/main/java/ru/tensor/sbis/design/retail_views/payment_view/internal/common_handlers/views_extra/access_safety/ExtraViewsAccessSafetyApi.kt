package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety

import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration

/** Обобщение API для безопасного изменения состояния View элементов "блок дополнительных действий". */
interface ExtraViewsAccessSafetyApi {
    /*# region VisibilityApi */
    /** Установка видимости [isVisible] кнопки "QR код". */
    fun setQrCodeButtonVisibility(isVisible: Boolean)

    /** Установка видимости [isVisible] кнопки "Ещё". */
    fun setMoreButtonVisibility(isVisible: Boolean)

    /** Установка видимости [isVisible] кнопки "Отправить". */
    fun setSendButtonVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на кнопку "QR код". */
    fun setQrCodeButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Ещё". */
    fun setMoreButtonEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на кнопку "Отправить". */
    fun setSendButtonEnabled(isEnabled: Boolean)
    /*# endregion */

    /*# region ConfigureViewsApi */
    /** Инициализация кнопки More. */
    fun configureMoreMenu(configuration: PopupMenuConfiguration)
    /*# endregion */
}