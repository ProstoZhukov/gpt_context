package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety

import android.view.View
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_dangerous.AllInputFieldsAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "все поля ввода". */
internal class AllInputFieldsAccessSafetyHandler(
    private val viewAccessApi: AllInputFieldsAccessDangerousApi
) : AllInputFieldsAccessSafetyApi {

    override fun clearAllFocus() {
        viewAccessApi.allInputFields.forEach(View::clearFocus)
    }
}