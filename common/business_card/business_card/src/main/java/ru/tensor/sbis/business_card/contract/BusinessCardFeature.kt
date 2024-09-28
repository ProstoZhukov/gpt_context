package ru.tensor.sbis.business_card.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card.presentation.view.BusinessCardFragment
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard

/**
 * Предоставление внешней функциональности модуля Визитка для других модулей
 */
class BusinessCardFeature : BusinessCardFragmentProvider {
    /**
     * Функция получения Fragment для отображения карточки визитки
     *
     * @return новый Fragment
     */
    override fun getBusinessCardFragment(params: BusinessCard): Fragment =
        BusinessCardFragment.newInstance(params)
}