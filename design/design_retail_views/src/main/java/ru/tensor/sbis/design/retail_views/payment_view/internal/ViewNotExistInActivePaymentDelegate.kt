package ru.tensor.sbis.design.retail_views.payment_view.internal

/** Ошибка возникает если запрашиваемая View отсутствует в разметке текущего делегата окна оплаты. */
internal class ViewNotExistInActivePaymentDelegate(viewName: String, viewResIdName: String) :
    RuntimeException(
        "Попытка доступа к View '$viewName' с идентификатором '$viewResIdName', " +
            "которая отсутствует в разметке текущего делегата окна оплаты!"
    )