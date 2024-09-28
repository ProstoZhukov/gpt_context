package ru.tensor.sbis.company_details_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Interface which provides CompanyDetailsFragment
 */
interface CompanyDetailsLiteFragmentProvider : Feature {

    /**
     * Creates CompanyDetailsFragment
     */
    fun getCompanyDetailsFragment(companyGroupUuid: UUID): Fragment?
}