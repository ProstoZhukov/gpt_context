package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety

/** Обобщение API для безопасного изменения состояния View "Ввод денежных средств". */
interface CashInputAccessSafetyApi {
    /*# region VisibilityApi */
    /** Переключение видимости [isVisible] заголовка "Сдача/ещё". */
    fun setChangeLabelVisibility(isVisible: Boolean)

    /** Переключение видимости [isVisible] значения "Сдача/ещё". */
    fun setChangeValueVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Установка возможности нажатия [isEnabled] на поле "Внесенные средства". */
    fun setMoneyInputEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на "Клавиатуру". */
    fun setKeyboardEnabled(isEnabled: Boolean)

    /** Установка возможности нажатия [isEnabled] на "Банкноты". */
    fun setBanknotesEnabled(isEnabled: Boolean)
    /*# endregion */

    /*# region TooltipApi */
    /** Показать подсказку с ошибкой в поле "Внесенные средства". */
    fun showMoneyInputErrorTooltip(errorText: String)
    /*# endregion */
}