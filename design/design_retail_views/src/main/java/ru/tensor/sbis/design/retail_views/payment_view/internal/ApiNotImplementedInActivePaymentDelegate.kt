package ru.tensor.sbis.design.retail_views.payment_view.internal

/** Ошибка возникающая в случае, попытке доступа к API, которое не реализовано ни одним из делегатов [PaymentView]. */
internal class ApiNotImplementedInActivePaymentDelegate(apiClassName: String) : RuntimeException(
    "Не найдена реализация для '$apiClassName' в текущем режиме работы PaymentView!"
)