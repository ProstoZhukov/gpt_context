package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous

import android.view.View
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.BanknotesCustomizedBinding
import ru.tensor.sbis.design.retail_views.money_input_field.MoneyInputEditableField
import ru.tensor.sbis.design.retail_views.money_view.MoneyView
import ru.tensor.sbis.design.retail_views.numberic_keyboard.NumericKeyboard
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Реализация объекта для прямого доступа к элементам управления ввода суммы. */
@DangerousApi
internal class CashInputAccessDangerousHandler(
    private val rootDelegateContainer: View
) : CashInputAccessDangerousApi {

    override val paymentChangeLabel: SbisTextView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_payment_change_label)

    override val paymentChangeValue: MoneyView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_payment_change_value)

    override val keyboardView: NumericKeyboard
        get() = rootDelegateContainer.findViewById(R.id.retail_views_keyboard_view)

    override val banknotesView: BanknotesCustomizedBinding by lazy {
        /* Особенности DataBinding'a и сочетания <merge>/<include>. */
        BanknotesCustomizedBinding.bind(
            rootDelegateContainer.findViewById(R.id.retail_views_banknotes_include)
        )
    }

    override val moneyInputField: MoneyInputEditableField
        get() = rootDelegateContainer.findViewById(R.id.retail_views_money_input_field)
}