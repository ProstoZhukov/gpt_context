package ru.tensor.sbis.design.retail_views.payment_view.modes.advance

import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.AdvanceActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.AdvanceRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.AdvanceSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.AdvanceViewAccessSafetyApi

/** Описание объекта, который обеспечивает работу режима "Аванс". */
interface AdvanceDelegateApi :
    AdvanceRenderApi,
    AdvanceSetDataApi,
    AdvanceViewAccessSafetyApi,
    AdvanceActionListenerApi
