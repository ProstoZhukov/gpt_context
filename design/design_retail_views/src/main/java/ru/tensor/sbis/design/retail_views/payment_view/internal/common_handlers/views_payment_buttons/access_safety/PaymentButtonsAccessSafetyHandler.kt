package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.tooltip.Tooltip
import ru.tensor.sbis.design.theme.Position

/** Реализация объекта для безопасного доступа к View элементам "кнопки оплаты". */
internal class PaymentButtonsAccessSafetyHandler(
    private val viewAccessApi: PaymentButtonsAccessDangerousApi
) : PaymentButtonsAccessSafetyApi {

    override fun setCheckButtonVisibility(isVisible: Boolean) {
        viewAccessApi.checkButton.isVisible = isVisible
    }

    override fun setCheckButtonInvisible(isInvisible: Boolean) {
        viewAccessApi.checkButton.isInvisible = isInvisible
    }

    override fun setCheckDoubleButtonVisibility(isVisible: Boolean) {
        viewAccessApi.checkDoubleButton.viewPropertiesApi.isVisible = isVisible
    }

    override fun setCheckDoubleButtonInvisible(isInvisible: Boolean) {
        viewAccessApi.checkDoubleButton.viewPropertiesApi.isInvisible = isInvisible
    }

    override fun setCardPaymentButtonVisibility(isVisible: Boolean) {
        viewAccessApi.payCardButton.viewPropertiesApi.isVisible = isVisible

        /*
            Хитрая штуковина для телефонной верстки.

            Помимо видимости 'payCardButton', необходимо дополнительно
            инвертировать видимость 'dummyGuidelinePortLayoutView' (доступна только в телефонной верстке в окне оплаты).
            Требуется для обеспечения "хитрой" привязки элементов в случае отсутствия кнопки "Оплата картой".
            https://online.sbis.ru/opendoc.html?guid=12a09896-f84b-4e45-ba44-e2839cb05db4&client=3
        */
        viewAccessApi.dummyPaymentButtonGuidelinePortLayoutView?.isVisible = !isVisible
    }

    override fun setCardPaymentButtonInvisible(isInvisible: Boolean) {
        viewAccessApi.payCardButton.viewPropertiesApi.isInvisible = isInvisible
    }

    override fun setCheckButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.checkButton.isEnabled = isEnabled
    }

    override fun setCheckDoubleButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.checkDoubleButton.viewPropertiesApi.isEnabled = isEnabled
    }

    override fun setPayCardDoubleButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.payCardButton.viewPropertiesApi.isEnabled = isEnabled
    }

    override fun showCardPaymentInputErrorTooltip(errorText: String) {
        val view = viewAccessApi.payCardButton.dangerousApi.editableView
        view.post {
            Tooltip.on(view, false).apply {
                setPosition(Position.TOP)
                setState(Tooltip.State.ERROR)
                setAccentBorderColor(R.attr.retail_views_main_red)
                setText(errorText)
                show()
            }
        }
    }
}