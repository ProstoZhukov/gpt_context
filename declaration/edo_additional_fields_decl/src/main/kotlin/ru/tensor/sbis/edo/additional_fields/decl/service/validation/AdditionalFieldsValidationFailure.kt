package ru.tensor.sbis.edo.additional_fields.decl.service.validation

/**
 * Ошибка валидации нескольких элементов [items] с одной и той же ошибкой, описанной в [userErrorMessage]
 *
 * @author sa.nikitin
 */
class AdditionalFieldsValidationFailure(
    val userErrorMessage: String,
    val items: List<AdditionalFieldsValidationItem>
)