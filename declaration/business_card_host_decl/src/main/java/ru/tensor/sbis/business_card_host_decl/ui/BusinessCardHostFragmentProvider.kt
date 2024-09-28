package ru.tensor.sbis.business_card_host_decl.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**@SelfDocumented*/
interface BusinessCardHostFragmentProvider : Feature {

    /**
     * Получить основной хост-фрагмент модуля визиток,
     * в зависимости от количества визиток будет определено, какой экран открыть следующим
     */
    fun getBusinessCardHostFragment(personUuid: UUID): Fragment
}