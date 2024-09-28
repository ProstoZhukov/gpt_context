package ru.tensor.sbis.business_card_list.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card_list.presentation.view.BusinessCardListFragment
import java.util.UUID

/**
 * Предоставление внешней функциональности модуля реестр визиток для других модулей
 */
class BusinessCardListFeature : BusinessCardListFragmentProvider {
    /**
     * Функция получения Fragment для отображения реестра визиток
     *
     * @return новый Fragment
     */
    override fun getBusinessCardListFragment(personUuid: UUID): Fragment =
        BusinessCardListFragment.newInstance(personUuid)
}