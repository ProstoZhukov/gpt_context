package ru.tensor.sbis.edo.additional_fields.decl.service

import ru.tensor.sbis.edo.additional_fields.decl.service.validation.AdditionalFieldsValidator

/**
 * Интерфейс сервисного слоя для доп полей
 * Документацию к свойствам можно смотреть в интерфейсах этих свойств
 *
 * Стандартная реализация расположена в модуле edo_additional_fields_integration репозитория android-edo
 */
interface AdditionalFieldsService {
    val editableInViewModeIdsProvider: AdditionalFieldsEditableInViewModeIdsProvider
    val conditionsChecker: AdditionalFieldsConditionsChecker
    val validator: AdditionalFieldsValidator
}