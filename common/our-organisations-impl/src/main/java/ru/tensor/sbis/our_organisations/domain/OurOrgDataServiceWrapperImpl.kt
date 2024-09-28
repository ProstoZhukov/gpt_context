package ru.tensor.sbis.our_organisations.domain

import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgDataServiceWrapper
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import java.util.UUID

/**
 * Реализация [OurOrgDataServiceWrapper] работает, как адаптер для [OurOrgListInteractor].
 *
 * @author mv.ilin
 */
internal class OurOrgDataServiceWrapperImpl(
    private val ourOrgListInteractor: OurOrgListInteractor
) : OurOrgDataServiceWrapper {
    override suspend fun getHeadOrganisation(organisationUUID: UUID): Organisation? {
        return ourOrgListInteractor.getHeadOrganisation(organisationUUID)
    }
}
