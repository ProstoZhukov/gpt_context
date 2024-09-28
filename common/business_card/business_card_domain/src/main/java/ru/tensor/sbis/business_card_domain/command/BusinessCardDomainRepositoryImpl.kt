package ru.tensor.sbis.business_card_domain.command

import ru.tensor.business.card.mobile.generated.BusinessCard
import ru.tensor.business.card.mobile.generated.BusinessCardManager
import ru.tensor.sbis.common.data.DependencyProvider
import java.util.UUID

/**
 * Имплементация интерфейса репозитория BusinessCardDomainRepository
 */
internal class BusinessCardDomainRepositoryImpl(
    private val manager: DependencyProvider<BusinessCardManager>
) : BusinessCardDomainRepository {

    override fun changePinState(businessCardUuid: UUID, pinState: Boolean) =
        manager.get().changePinState(businessCardUuid, pinState)

    override fun getBusinessCard(): BusinessCard? = manager.get().checkCount().card
}
