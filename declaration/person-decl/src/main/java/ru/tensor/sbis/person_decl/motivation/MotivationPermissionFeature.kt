package ru.tensor.sbis.person_decl.motivation

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/** Фича запроса доступа к разделам мотивации. */
interface MotivationPermissionFeature : Feature {

    /** Проверка прав на зарплату другого сотрудника */
    fun hasSalaryPersonAccess(personUUID: UUID): Flow<Boolean>

    /** Проверка прав на мотивацию для текущего пользователя. */
    fun hasMotivationAccessForCurrentUser(): Flow<Boolean>

    /** @SelfDocumented*/
    fun hasKpiAccess(personUUID: UUID): Flow<Boolean>

    /** @SelfDocumented*/
    fun hasRatingsAccess(personUUID: UUID): Flow<Boolean>
}