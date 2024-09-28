package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners

import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.PaymentUtils
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import java.math.BigDecimal

/** Общая реализация объекта для обработки действий пользователя с View элементами - "кнопки оплаты". */
internal class PaymentButtonsActionListenerHandler(
    private val viewAccessApi: PaymentButtonsAccessDangerousApi
) : PaymentButtonsActionListenerApi {

    /** Если двойная кнопка 'Картой' находится в режиме редактирования, то это смешанная оплата. */
    private val isMixPaymentMode: Boolean
        get() = viewAccessApi.payCardButton.viewPropertiesApi.isEditMode

    override fun setCardPaymentClickListener(action: (() -> Unit)) {
        viewAccessApi.payCardButton.actionApi.onFullButtonClickAction = action
    }

    override fun setDoubleButtonCheckAmountChangedListener(action: (checkAmount: BigDecimal) -> Unit) {
        viewAccessApi.checkDoubleButton.actionApi.onMoneyChangedAction = { rawValue ->
            PaymentUtils.convertUserInputToBigDecimalOrNull(rawValue)?.let {
                action.invoke(it)
            }
        }
    }

    override fun setCheckClickListener(action: (mixPaymentMode: Boolean) -> Unit) {
        var hasOneHandler = false

        /* Обрабатываем ошибки отсутствия View в делегате. */
        try {
            viewAccessApi.checkButton.preventDoubleClickListener(LONG_CLICK_DELAY) {
                action.invoke(isMixPaymentMode)
            }

            hasOneHandler = true
        } catch (e: ViewNotExistInActivePaymentDelegate) {
            /* Ignore error. */
        }

        try {
            viewAccessApi.checkDoubleButton.actionApi.onIconClickAction = {
                action.invoke(isMixPaymentMode)
            }

            hasOneHandler = true
        } catch (e: ViewNotExistInActivePaymentDelegate) {
            /* Ignore error. */
        }

        /* Если не было предоставлено ни одного обработчика, то роняем приложение. */
        if (!hasOneHandler) {
            throw ViewNotExistInActivePaymentDelegate(
                viewName = "Не найдено ни одной из View: (checkButton, checkDoubleButton)",
                viewResIdName = ""
            )
        }
    }
}