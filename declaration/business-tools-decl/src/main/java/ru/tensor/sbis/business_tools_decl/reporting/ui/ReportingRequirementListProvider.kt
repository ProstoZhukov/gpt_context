package ru.tensor.sbis.business_tools_decl.reporting.ui

import androidx.fragment.app.Fragment

/**
 * Интерфейс провайдер реестр требований
 *
 * @author ae.noskov
 */
interface ReportingRequirementListProvider {

    /**
     * Возвращает фрагмент реестра требований
     */
    fun getRequirementListFragment(): Fragment

}