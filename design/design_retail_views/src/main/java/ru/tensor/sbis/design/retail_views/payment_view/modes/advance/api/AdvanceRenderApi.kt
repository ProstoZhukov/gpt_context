package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api

import ru.tensor.sbis.design.retail_views.databinding.RetailViewsAdvanceLayoutBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi

/** Интерфейс для описания Api отрисовки делегата "Аванс" */
interface AdvanceRenderApi : BaseRenderApi<RetailViewsAdvanceLayoutBinding, AdvanceInitializeApi> {

    /** Объект предоставляющий доступ к API [AdvanceRenderApi.Handler]. */
    override val renderApiHandler: Handler

    /** Интерфейс объекта предоставляющего доступ к API [AdvanceRenderApi]. */
    interface Handler : BaseRenderApi.Handler<RetailViewsAdvanceLayoutBinding, AdvanceInitializeApi>
}