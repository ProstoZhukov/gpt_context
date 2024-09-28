package ru.tensor.sbis.business_card_domain.command

import ru.tensor.business.card.mobile.generated.BusinessCard
import ru.tensor.business.card.mobile.generated.PinException
import ru.tensor.sbis.CXX.SbisException
import java.util.UUID

/**
 * Wrapper команд для контроллера для работы с визиткой.
 */
interface BusinessCardDomainCommandWrapper {

    /**
     * Сменить состояние пина
     * @throws PinException если произошла ошибка при изменении состояния пина
     * @throws SbisException если произошла ошибка при выполнении операции
     */
    @Throws(PinException::class, SbisException::class)
    suspend fun changePinState(businessCardUuid: UUID, pinState: Boolean)

    /**
     * Получить модель данных визитки, если она одна в списке,
     * если больше одной или нет визиток в списке - метод вернет null
     */
    fun getBusinessCard(): BusinessCard?
}