package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.dangerous

import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_dangerous.AllInputFieldsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.AdvanceDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.AdvanceViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с прямым доступом к элементам [AdvanceDelegate].
 *
 * В случае, если в текущем режиме работы View элемент не может быть найден, будет выброшен
 * exception [ViewNotExistInActivePaymentDelegate].
 *
 * ВАЖНО! Используйте данной API на свой страх и риск, прямой доступ к элементам может привести
 * к непредсказуемым последствиям и сломать работу [AdvanceDelegate].
 * Предпочтительно использовать [AdvanceViewAccessSafetyApi].
 */
internal interface AdvanceViewAccessDangerousApi : BaseViewAccessDangerousApi {

    /** Объект предоставляющий доступ к API [AdvanceViewAccessDangerousApi.Handler]. */
    override val viewAccessApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [AdvanceViewAccessDangerousApi]. */
    interface Handler :
        BaseViewAccessDangerousApi.Handler,
        CashInputAccessDangerousApi,
        MixPaymentAccessDangerousApi,
        ExtraViewsAccessDangerousApi,
        PaymentTypeAccessDangerousApi,
        CommentInfoAccessDangerousApi,
        ToolbarViewsAccessDangerousApi,
        PaymentButtonsAccessDangerousApi,
        AllInputFieldsAccessDangerousApi {

        /** Получение прямого доступа [PaymentView]. */
        val paymentView: PaymentView
            @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
    }
}