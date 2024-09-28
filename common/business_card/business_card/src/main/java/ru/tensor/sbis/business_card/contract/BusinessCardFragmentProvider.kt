package ru.tensor.sbis.business_card.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
interface BusinessCardFragmentProvider : Feature {

    /** Функция получения Fragment для отображения экрана визитки */
    fun getBusinessCardFragment(params: BusinessCard): Fragment
}