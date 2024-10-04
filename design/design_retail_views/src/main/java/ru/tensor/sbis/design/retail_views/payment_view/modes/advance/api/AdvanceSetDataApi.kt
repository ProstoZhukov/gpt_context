package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.set_data.ExtraViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.inner_types.AdvancePaymentInnerMode

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface AdvanceSetDataApi : BaseSetDataApi {

    /** Объект предоставляющий доступ к API [AdvanceSetDataApi.Handler]. */
    override val setDataApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [AdvanceSetDataApi]. */
    interface Handler :
        BaseSetDataApi.Handler,
        CashInputSetDataApi,
        ExtraViewsSetDataApi,
        PaymentTypeSetDataApi,
        CommentInfoSetDataApi,
        ToolbarViewsSetDataApi,
        PaymentButtonsSetDataApi,
        PaymentTypeSetDataApi.TypeChanger<AdvancePaymentInnerMode>
}