package ru.tensor.sbis.business_card_list.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**@SelfDocumented*/
interface BusinessCardListFragmentProvider : Feature {

    /** Функция получения Fragment для отображения реестра визиткок */
    fun getBusinessCardListFragment(personUuid: UUID): Fragment
}