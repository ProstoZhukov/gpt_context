package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data

import android.widget.EditText
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import java.math.BigDecimal

/** Реализация объекта для установки данных в блок "кнопки оплаты". */
internal class PaymentButtonsSetDataHandler(
    private val viewAccessApi: PaymentButtonsAccessDangerousApi
) : PaymentButtonsSetDataApi {

    private val keyboardHelper by lazy { CustomNumericKeyboardHelper() }

    override fun setCheckButtonTextValue(amount: BigDecimal) {
        viewAccessApi.checkButton.setTitle(getAmountText(amount))
    }

    override fun setCheckDoubleButtonTextValue(amount: BigDecimal) {
        viewAccessApi.checkDoubleButton.viewPropertiesApi.setInputTextValue(getAmountText(amount))
        viewAccessApi.checkDoubleButton.dangerousApi.editableView.updateCursorOffset()
    }

    override fun setPayCardValueTextValue(amount: BigDecimal) {
        viewAccessApi.payCardButton.viewPropertiesApi.setInputTextValue(getAmountText(amount))
        viewAccessApi.payCardButton.dangerousApi.editableView.updateCursorOffset()
    }

    override fun dropPayCardValue() {
        viewAccessApi.payCardButton.viewPropertiesApi.setInputTextValue("")
    }

    private fun getAmountText(amount: BigDecimal): String =
        amountFormat.format(amount)

    private fun EditText.updateCursorOffset() {
        keyboardHelper.updateCursorOffset(this, CustomNumericKeyboardHelper.FieldType.MONEY)
    }
}