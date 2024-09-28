package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseSetDataApi

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface DepositWithdrawalSetDataApi : BaseSetDataApi {

    /** Объект предоставляющий доступ к API [DepositWithdrawalSetDataApi.Handler]. */
    override val setDataApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DepositWithdrawalSetDataApi]. */
    interface Handler :
        BaseSetDataApi.Handler,
        CommentInfoSetDataApi
}