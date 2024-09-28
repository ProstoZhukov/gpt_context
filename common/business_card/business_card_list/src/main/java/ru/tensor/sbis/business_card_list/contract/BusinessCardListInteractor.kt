package ru.tensor.sbis.business_card_list.contract

import java.util.UUID

/**@SelfDocumented */
internal interface BusinessCardListInteractor {

    /**
     * Произошло изменение состояния пина визитки
     */
    suspend fun onPinStateChanged(businessCardUuid: UUID, pinState: Boolean)
}