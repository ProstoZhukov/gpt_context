package ru.tensor.sbis.business_card_host.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainCommandWrapper
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostInteractor
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.business_card_list.di.view.map

/** @SelfDocumented */
internal class BusinessCardHostInteractorImpl(
    private val commandWrapper: BusinessCardDomainCommandWrapper
) : BusinessCardHostInteractor {

    override suspend fun getBusinessCard(): BusinessCard? =
        withContext(Dispatchers.IO) {
            commandWrapper.getBusinessCard()?.map()
        }
}