package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety

/** Обобщение API для безопасного изменения состояния View "переключение режима оплаты". */
interface MixPaymentAccessSafetyApi {
    /*# region VisibilityApi */
    /**
     * Переключение видимости кнопок включения смешанной оплаты,
     * в зависимости от текущего [isMixedMode] состояния режима оплаты.
     */
    fun setCurrentMixPaymentMode(isMixedMode: Boolean)

    /** Переключение видимости [isVisible] кнопки включения смешанной оплаты. */
    fun setEnableMixButtonVisibility(isVisible: Boolean)

    /** Переключение видимости [isVisible] кнопки выключения смешанной оплаты. */
    fun setDisableMixButtonVisibility(isVisible: Boolean)
    /*# endregion */

    /*# region EnableApi */
    /** Переключение режима доступности [isEnabled] кнопки включения смешанной оплаты. */
    fun setEnableMixButtonEnableState(isEnabled: Boolean)

    /** Переключение режима доступности [isEnabled] кнопки выключения смешанной оплаты. */
    fun setDisableMixButtonEnableState(isEnabled: Boolean)
    /*# endregion */
}