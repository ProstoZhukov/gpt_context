package ru.tensor.sbis.edo.additional_fields.decl.model.raw_data

import java.util.UUID

/** @SelfDocumented */
data class FieldRawData(
    val id: UUID,
    val title: String,
    val folderId: UUID,
    val valueValidationCondition: String?,
    val visibilityCondition: String?,
    val stages: List<UUID>,
    val placeholder: String,
    val variableName: String,
    val isRequired: Boolean,
    val typeName: String,
    val kindName: String?
)