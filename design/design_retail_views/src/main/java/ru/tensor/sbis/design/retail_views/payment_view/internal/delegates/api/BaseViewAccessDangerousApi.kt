package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api

import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/**
 * Интерфейс для объединения Api компонента связанного с прямым доступом к элементам [PaymentView].
 *
 * В случае, если в текущем режиме работы View элемент не может быть найден, будет выброшен
 * exception [ViewNotExistInActivePaymentDelegate].
 *
 * ВАЖНО! Используйте данной API на свой страх и риск, прямой доступ к элементам может привести
 * к непредсказуемым последствиям и сломать работу [PaymentView].
 * Предпочтительно использовать [BaseViewAccessSafetyApi].
 */
interface BaseViewAccessDangerousApi {

    /** Объект предоставляющий доступ к API [BaseViewAccessDangerousApi.Handler]. */
    val viewAccessApi: Handler

    /** Маркерный-интерфейс объекта предоставляющего доступ к API [BaseViewAccessDangerousApi]. */
    interface Handler
}