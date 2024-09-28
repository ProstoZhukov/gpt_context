package ru.tensor.sbis.business_card_domain.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.business.card.mobile.generated.BusinessCard
import java.util.UUID

/**
 * Wrapper CRUD команд контролера
 */
internal class BusinessCardDomainCommandWrapperImpl(
    private val repository: BusinessCardDomainRepository
) : BusinessCardDomainCommandWrapper {

    /** Метод для изменения состояния пина визитки */
    override suspend fun changePinState(businessCardUuid: UUID, pinState: Boolean) {
        withContext(Dispatchers.IO) {
            repository.changePinState(businessCardUuid, pinState)
        }
    }

    override fun getBusinessCard(): BusinessCard? =
        repository.getBusinessCard()
}
