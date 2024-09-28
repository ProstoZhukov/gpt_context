package ru.tensor.sbis.design.selection.ui.model.recipient

import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Модель данных для отображения контрагента в компоненте выбора
 *
 * @author us.bessonov
 */
interface ContractorSelectorItemModel : SelectorItemModel {

    /** @SelfDocumented */
    val photoData: CompanyData
}