package ru.tensor.sbis.design.retail_views.payment_view

import ru.tensor.sbis.design.retail_views.payment_view.modes.PaymentViewMode
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.AdvanceDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.DebtCreditDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.DepositWithdrawalDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.PaymentDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.RefundPaymentDelegateApi

/** Интерфейс объединяющий все доступное в [PaymentView] API. */
interface PaymentViewApi {

    /** Объект предоставляющий доступ к API [PaymentView]. */
    val api: Accessor

    /** Получить текущий режим работы окна оплаты. */
    val paymentMode: PaymentViewMode?

    /** Интерфейс объекта предоставляющего точечный доступ к делегатам [PaymentView] для их тонкой настройки. */
    interface Accessor {

        /** Доступ к режиму "Аванс". */
        val advanceApi: AdvanceDelegateApi

        /** Доступ к режиму "Оплата кредита (Долги)". */
        val debtCreditApi: DebtCreditDelegateApi

        /** Доступ к режиму "Внесение/Изъятие". */
        val depositWithDrawApi: DepositWithdrawalDelegateApi

        /** Доступ к режиму "Оплата/Расчет". */
        val paymentApi: PaymentDelegateApi

        /** Доступ к режиму "Возврат". */
        val refundPaymentApi: RefundPaymentDelegateApi
    }
}