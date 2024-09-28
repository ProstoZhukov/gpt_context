package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous

import ru.tensor.sbis.design.retail_views.databinding.BanknotesCustomizedBinding
import ru.tensor.sbis.design.retail_views.money_input_field.MoneyInputEditableField
import ru.tensor.sbis.design.retail_views.money_view.MoneyView
import ru.tensor.sbis.design.retail_views.numberic_keyboard.NumericKeyboard
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Обобщение API для доступа к элементам управления ввода суммы. */
interface CashInputAccessDangerousApi {

    /** Получение прямого доступа к View "Сдача/Ещё". */
    val paymentChangeLabel: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к View "Кол-во сдачи/ещё". */
    val paymentChangeValue: MoneyView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к View "Клавиатура". */
    val keyboardView: NumericKeyboard
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к View "Банкноты". */
    val banknotesView: BanknotesCustomizedBinding
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к полю "Внесенные средства". */
    val moneyInputField: MoneyInputEditableField
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}