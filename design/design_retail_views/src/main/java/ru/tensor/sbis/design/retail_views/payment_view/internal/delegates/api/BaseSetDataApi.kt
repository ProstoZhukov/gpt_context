package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface BaseSetDataApi {

    /** Объект предоставляющий доступ к API [BaseSetDataApi.Handler]. */
    val setDataApi: Handler

    /** Маркерный-интерфейс объекта предоставляющего доступ к API [BaseSetDataApi]. */
    interface Handler
}