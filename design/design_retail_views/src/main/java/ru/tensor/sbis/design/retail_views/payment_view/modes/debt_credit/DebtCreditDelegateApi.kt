package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit

import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditViewAccessSafetyApi

/** Описание объекта, который обеспечивает работу режима "Оплата кредита (Долги)". */
interface DebtCreditDelegateApi :
    DebtCreditRenderApi,
    DebtCreditSetDataApi,
    DebtCreditActionListenerApi,
    DebtCreditViewAccessSafetyApi