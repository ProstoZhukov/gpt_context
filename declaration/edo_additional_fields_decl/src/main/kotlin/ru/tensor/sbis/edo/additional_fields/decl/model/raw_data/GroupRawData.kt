package ru.tensor.sbis.edo.additional_fields.decl.model.raw_data

import java.util.UUID

/** @SelfDocumented */
data class GroupRawData(
    val id: UUID,
    val title: String,
    val folderId: UUID,
    val valueValidationCondition: String?,
    val visibilityCondition: String?,
    val stages: List<UUID>,
)