package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data

/** Обобщение API для установки данных в шапке окна оплаты. */
interface ToolbarViewsSetDataApi {

    /** Установка имени клиента [clientName] в кнопку "Клиент". */
    fun setClientName(clientName: String?)
}