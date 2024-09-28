package ru.tensor.sbis.our_organisations.domain

import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.presentation.list.ui.ListResultWrapper
import ru.tensor.sbis.ourorg.generated.ListResultOfOrganizationMapOfStringString
import ru.tensor.sbis.ourorg.generated.Organization
import ru.tensor.sbis.ourorg.generated.OurorgFilter

/**
 * Объект для маппинга моделей.
 *
 * @author mv.ilin
 */
internal object OrganisationMapper {
    /**
     * Маппинг из модели контроллера в модель представления.
     *
     * @param organization
     */
    fun fromController(organization: Organization): Organisation {
        return Organisation(
            originalId = organization.identifier,
            uuid = organization.guid!!,
            parentId = organization.parentId,
            parentUUID = organization.parent,
            name = organization.name,
            inn = organization.inn,
            kpp = organization.kpp,
            branchCode = organization.branchCode,
            isFolder = organization.isFolder,
            isEliminated = organization.isEliminated,
            isPrimaryOrg = organization.isPrimaryOrg,
            isSalePoint = organization.isSalesPoint
        )
    }

    /**
     * Маппинг из списка моделей организаций контроллера в список моделей представления.
     *
     * @param list
     */
    fun fromControllerList(list: ListResultOfOrganizationMapOfStringString): ListResultWrapper<Organisation> {
        return ListResultWrapper(
            result = list.result.map { fromController(it) }.toMutableList(),
            haveMore = list.haveMore
        )
    }

    /**
     * Маппинг фильтра представления в модель контроллера.
     *
     * @param filter
     */
    fun toControllerFilter(filter: OurOrgFilter): OurorgFilter {
        return OurorgFilter().apply {
            searchString = filter.searchString
            ids = ArrayList(filter.ids.map { it.toLong() })
            parent = filter.parent
            addSalesPoints = filter.addSalesPoints
            salesPointsOnly = filter.salePointOnly
            primaryOrgOnly = filter.primaryOrgOnly
            headsOnly = filter.headsOnly
            from = filter.offset
            count = filter.count
            withEliminated = filter.withEliminated
            scopesAreas = ArrayList(filter.scopesAreas)
            showInnerCompany = filter.showInnerCompany
        }
    }
}
