package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers

/** Описание Api для инициализации Views, которые могут быть переиспользованы между делегатами окна оплаты. */
fun interface IncludeViewsInitializeApi {
    /** Запустить процесс инициализации. */
    fun initialize()
}