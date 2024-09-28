package ru.tensor.sbis.edo.additional_fields.decl.model

import ru.tensor.sbis.common.util.illegalState
import java.util.UUID

/**
 * Модель списка дополнительных элементов - полей и групп полей
 *
 * @author sa.nikitin
 */
data class AdditionalItemsModels(
    val modelsGroups: Map<UUID, List<AdditionalItemModel>>,
    val settings: List<AdditionalItemsSettings>,
    val selectedModelsGroupId: UUID? = modelsGroups.keys.firstOrNull()
)

/** @SelfDocumented */
fun AdditionalItemsModels.selectedModelsGroup(): Map.Entry<UUID, List<AdditionalItemModel>>? =
    selectedModelsGroupId
        ?.let { selectedModelsGroupId ->
            modelsGroups.entries.firstOrNull { it.key == selectedModelsGroupId }
                ?: kotlin.run {
                    illegalState { "Models group with id $selectedModelsGroupId not found" }
                    null
                }
        }
        ?: modelsGroups.entries.firstOrNull()

/** @SelfDocumented */
fun AdditionalItemsModels.selectedModels(): List<AdditionalItemModel> = selectedModelsGroup()?.value ?: emptyList()

fun AdditionalItemsModels.logDesc(): String =
    "${modelsGroups.mapValues { it.value.size }}, selectedGroupId - $selectedModelsGroupId"