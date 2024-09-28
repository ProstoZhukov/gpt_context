package ru.tensor.sbis.communicator.contacts_declaration.model.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile

/**
 * Модель результата запросов сотрудников
 *
 * @author vv.chekurda
 */
@Parcelize
data class EmployeeListResult(
    val employees: List<EmployeeProfile> = emptyList(),
    val hasMore: Boolean = false,
    val nameHighlight: List<List<Int>> = listOf()
) : Parcelable