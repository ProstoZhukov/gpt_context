package ru.tensor.sbis.crud.sbis.pricing.model

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.pricing.generated.PriceEntityType
import java.util.*
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel as ControllerAvailablePriceListModel

/**
 * Модель доступной цены
 *
 * @param key UUID - Uuid модели
 * @param priceListId Long - Идентификатор прайс-листа
 * @param name String - Наименование прайс-листа
 * @param localStatus LocalStatus - Локальный статус модели
 * @param type тип прайс листа
 * @param isLinked Boolean - Флажок, привязан ли прайс к точке продаж указаной в фильтре. Если в фильтре при вызове метода list() или refresh() идентификатор точки продаж не передан, значения флага для всех элементов будет false
 *
 * @see UUID
 * @see LocalStatus
 */
data class AvailablePriceList(val key: UUID,
                              val priceListId: Long,
                              val name: String?,
                              val localStatus: LocalStatus?,
                              val type: PriceEntityType?,
                              var isLinked: Boolean) {

    companion object {
        fun stub(): AvailablePriceList = AvailablePriceList(
                UUIDUtils.NIL_UUID,
                0,
                "",
                LocalStatus.LS_SYNCHRONIZED,
                PriceEntityType.PRICELIST_HIERARCHIC,
                false)
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerAvailablePriceListModel.map() = AvailablePriceList(
        key!!,
        pricelistId!!,
        name,
        localStatus?.map(),
        type,
        isLinked)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun AvailablePriceList.map() = ControllerAvailablePriceListModel(
        key,
        priceListId,
        name,
        type,
        localStatus?.map(),
        isLinked)
