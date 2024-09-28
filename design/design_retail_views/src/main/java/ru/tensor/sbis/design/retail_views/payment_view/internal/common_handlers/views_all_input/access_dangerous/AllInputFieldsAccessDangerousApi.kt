package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_dangerous

import android.widget.EditText

/** Обобщение API для доступа к View элементам "все поля ввода". */
interface AllInputFieldsAccessDangerousApi {

    /** Получение доступа ко всем полям ввода, которые поддерживает делегат. */
    val allInputFields: List<EditText>
}