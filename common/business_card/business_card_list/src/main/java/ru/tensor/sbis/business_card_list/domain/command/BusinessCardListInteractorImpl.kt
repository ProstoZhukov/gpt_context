package ru.tensor.sbis.business_card_list.domain.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainCommandWrapper
import ru.tensor.sbis.business_card_list.contract.BusinessCardListInteractor
import java.util.UUID


internal class BusinessCardListInteractorImpl(
    private val commandWrapper: BusinessCardDomainCommandWrapper
) : BusinessCardListInteractor {

    override suspend fun onPinStateChanged(businessCardUuid: UUID, pinState: Boolean) =
        withContext(Dispatchers.IO) {
            commandWrapper.changePinState(businessCardUuid, pinState)
        }
}