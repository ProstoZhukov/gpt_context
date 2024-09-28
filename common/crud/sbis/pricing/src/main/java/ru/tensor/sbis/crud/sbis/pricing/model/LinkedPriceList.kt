package ru.tensor.sbis.crud.sbis.pricing.model

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.pricing.generated.PriceEntityType
import java.util.*
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel as ControllerLinkedPriceListModel

/**
 * Модель связной цены
 *
 * @param key UUID - Uuid модели
 * @param priceListId Long - Идентификатор прайс листа
 * @param name String - Наименование прайс листа
 * @param wareHouseId Long - Идентификатор склада, к которому привязан прайс-лист
 * @param type тип прайс листа
 * @param localStatus LocalStatus - Локальный статус модели
 *
 * @see UUID
 * @see LocalStatus
 */
data class LinkedPriceList(val key: UUID,
                           val priceListId: Long,
                           val name: String?,
                           val salePointID: Long?,
                           val type: PriceEntityType?,
                           val localStatus: LocalStatus?) {

    companion object {
        fun stub(): LinkedPriceList = LinkedPriceList(
                UUIDUtils.NIL_UUID,
                0,
                "",
                0,
                PriceEntityType.PRICELIST_HIERARCHIC,
                LocalStatus.LS_SYNCHRONIZED)
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerLinkedPriceListModel.map() = LinkedPriceList(
        key!!,
        pricelistId!!,
        name,
        salePointId,
        type,
        localStatus?.map())

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun LinkedPriceList.map() = ControllerLinkedPriceListModel(
        key,
        priceListId,
        name,
        salePointID,
        type,
        localStatus?.map())
