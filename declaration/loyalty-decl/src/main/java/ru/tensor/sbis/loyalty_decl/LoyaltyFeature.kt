package ru.tensor.sbis.loyalty_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.loyalty_decl.model.LoyaltyProgramBaseInfo
import ru.tensor.sbis.loyalty_decl.model.OperationConfiguration

/** Функциональность лояльности. */
interface LoyaltyFeature : Feature {

    /** Создать фрагмент для экрана программ лояльности. */
    fun createLoyaltyProgramsFragment(config: OperationConfiguration): Fragment

    /** Создать окно с информацией о скидке.  */
    fun createDiscountInfoContainer(loyaltyProgram: LoyaltyProgramBaseInfo): SbisContainer
}
