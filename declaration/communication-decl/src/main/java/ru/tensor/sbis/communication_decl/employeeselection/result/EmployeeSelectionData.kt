package ru.tensor.sbis.communication_decl.employeeselection.result

import java.util.*

/**
 * Данные выбранного сотрудника/подразделения
 *
 * Свойства [photoId] и [position] имеются только для [EmployeeSelectionItemType.PERSON]
 */
data class EmployeeSelectionData(
    val uuid: UUID,
    val name: String,
    val faceId: Long,
    val photoId: String?,
    val photoUrl: String?,
    val position: String?,
    val type: EmployeeSelectionItemType
)