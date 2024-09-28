package ru.tensor.sbis.business_card_domain.command

import ru.tensor.business.card.mobile.generated.BusinessCard
import ru.tensor.business.card.mobile.generated.PinException
import ru.tensor.sbis.CXX.SbisException
import java.util.UUID

/**
 * Интерфейс для связи с контроллером.
 */
interface BusinessCardDomainRepository {

    /** Сменить состояние пина */
    @Throws(PinException::class, SbisException::class)
    fun changePinState(businessCardUuid: UUID, pinState: Boolean)

    /**
     * Получить модель данных визитки, если она одна в списке,
     * если больше одной или нет визиток в списке - метод вернет null
     */
    fun getBusinessCard(): BusinessCard?
}
