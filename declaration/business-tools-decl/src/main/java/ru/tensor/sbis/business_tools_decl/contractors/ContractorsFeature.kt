package ru.tensor.sbis.business_tools_decl.contractors

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс, описывающий функционал, который доступен внешним модулям
 *
 * @author s.r.golovkin
 */
interface ContractorsFeature: Feature {

    /**
     * Предоставить фрагмент реестра компаний.
     * @return [Fragment], отображающий реестр компаний
     */
    fun getContractorListFragment(): Fragment

}