package ru.tensor.sbis.business_card_host.contract.internal.list

import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard

/**@SelfDocumented*/
internal interface BusinessCardHostInteractor {

    /**
     * Получить модель данных визитки, если она одна в списке,
     * если больше одной или нет визиток в списке - метод вернет null
     */
    suspend fun getBusinessCard(): BusinessCard?
}