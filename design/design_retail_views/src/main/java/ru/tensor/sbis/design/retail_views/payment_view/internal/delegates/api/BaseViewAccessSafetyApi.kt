package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api

/**
 * Интерфейс для объединения Api компонента связанного с безопасным
 * доступом к View-параметрам элементов интерфейса.
 */
interface BaseViewAccessSafetyApi {

    /** Объект предоставляющий доступ к API [BaseViewAccessSafetyApi.Handler]. */
    val viewSafetyApi: Handler

    /** Маркерный-интерфейс объекта предоставляющего доступ к API [BaseViewAccessSafetyApi]. */
    interface Handler
}