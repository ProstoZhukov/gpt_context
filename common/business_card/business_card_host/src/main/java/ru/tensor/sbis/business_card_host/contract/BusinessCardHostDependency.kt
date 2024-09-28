package ru.tensor.sbis.business_card_host.contract

import ru.tensor.sbis.business_card.contract.BusinessCardFragmentProvider
import ru.tensor.sbis.business_card_list.contract.BusinessCardListFragmentProvider

/**
 * Получение внешних зависимостей модуля
 */
interface BusinessCardHostDependency :
    BusinessCardListFragmentProvider,
    BusinessCardFragmentProvider