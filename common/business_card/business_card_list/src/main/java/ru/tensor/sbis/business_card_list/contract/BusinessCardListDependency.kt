package ru.tensor.sbis.business_card_list.contract

import ru.tensor.sbis.business_card.contract.BusinessCardFragmentProvider
import ru.tensor.sbis.link_share.ui.LinkShareFragmentProvider

/**
 * Получение внешних зависимостей модуля реестр визиток от других модулей
 */
interface BusinessCardListDependency :
    LinkShareFragmentProvider,
    BusinessCardFragmentProvider