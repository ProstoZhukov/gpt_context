package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api

/**
 * Класс для объединения Api компонента связанного с установкой
 * слушателей и действий реализующих кастомное поведение.
 */
interface BaseActionListenerApi {

    /** Объект предоставляющий доступ к API [BaseActionListenerApi.Handler]. */
    val actionListenerApi: Handler

    /** Маркерный-интерфейс объекта предоставляющего доступ к API [BaseActionListenerApi]. */
    interface Handler
}