package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety

import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isInvisible
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.tooltip.Tooltip
import ru.tensor.sbis.design.theme.Position

/** Реализация объекта для безопасного доступа к элементам управления "Ввод денежных средств". */
internal class CashInputAccessSafetyHandler(
    private val keyboardHelper: CustomNumericKeyboardHelper,
    private val viewAccessApi: CashInputAccessDangerousApi
) : CashInputAccessSafetyApi {

    override fun setChangeLabelVisibility(isVisible: Boolean) {
        viewAccessApi.paymentChangeLabel.isInvisible = !isVisible
    }

    override fun setChangeValueVisibility(isVisible: Boolean) {
        viewAccessApi.paymentChangeValue.isInvisible = !isVisible
    }

    override fun setMoneyInputEnabled(isEnabled: Boolean) {
        viewAccessApi.moneyInputField.isEnabled = isEnabled

        /* Отключаем работу с нашей клавиатурой. */
        if (!isEnabled) {
            keyboardHelper.detachInputFieldIfSame(
                inputFieldToDetach = viewAccessApi.moneyInputField.editableView
            )
        }
    }

    override fun setKeyboardEnabled(isEnabled: Boolean) {
        viewAccessApi.keyboardView.isEnabled = isEnabled
    }

    override fun setBanknotesEnabled(isEnabled: Boolean) {
        viewAccessApi.banknotesView.root.isEnabled = isEnabled
        (viewAccessApi.banknotesView.root as? ViewGroup)?.forEach { child -> child.isEnabled = isEnabled }
    }

    override fun showMoneyInputErrorTooltip(errorText: String) {
        viewAccessApi.moneyInputField.post {
            Tooltip.on(viewAccessApi.moneyInputField, false).apply {
                setPosition(Position.BOTTOM)
                setState(Tooltip.State.ERROR)
                setAccentBorderColor(R.attr.retail_views_main_red)
                setText(errorText)
                show()
            }
        }
    }
}