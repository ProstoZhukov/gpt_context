package ru.tensor.sbis.our_organisations.feature.data

import java.util.UUID

/**
 * Контракт для предоставленияи доступа к сервису организаций.
 *
 * @author aa.mezencev
 */
interface OurOrgDataServiceWrapper {

    /**
     * Получить головную организацию.
     *
     * @param organisationUUID идентификтор организации.
     */
    suspend fun getHeadOrganisation(organisationUUID: UUID): Organisation?
}