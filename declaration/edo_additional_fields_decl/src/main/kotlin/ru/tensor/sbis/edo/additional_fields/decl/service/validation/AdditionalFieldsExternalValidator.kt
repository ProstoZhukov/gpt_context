package ru.tensor.sbis.edo.additional_fields.decl.service.validation

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Внешний валидатор доп полей
 *
 * Может понадобиться, например, при попытке выполнить переход через ДЗЗ с незаполненными доп полями.
 * В таком случае валидация будет проведена внутри компонента ДЗЗ,
 * но обработать ошибки нужно внутри компонента доп полей
 */
interface AdditionalFieldsExternalValidator : Feature {

    val additionalFieldsValidationFailures: Flow<List<AdditionalFieldsValidationFailure>>
}