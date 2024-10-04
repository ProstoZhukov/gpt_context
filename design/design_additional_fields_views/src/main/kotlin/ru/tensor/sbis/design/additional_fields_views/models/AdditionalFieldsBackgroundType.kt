package ru.tensor.sbis.design.additional_fields_views.models

/**
 * Расположение доп. поля в списке.
 *
 * @author au.aleksikov
 */
enum class AdditionalFieldsBackgroundType {
    /**
     * Первое доп. поле
     */
    FIRST,

    /**
     * Последнее доп. поле
     */
    LAST,

    /**
     * Доп. поле в середине списка
     */
    CENTER,

    /**
     * Единственное доп. поле
     */
    ONCE
}