package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety

/** Обобщение API для безопасного изменения состояния View элементов "все поля ввода". */
interface AllInputFieldsAccessSafetyApi {
    /*# region FocusViewsApi */
    /** Сбросить фокус у всех полей ввода. */
    fun clearAllFocus()
    /*# endregion */
}