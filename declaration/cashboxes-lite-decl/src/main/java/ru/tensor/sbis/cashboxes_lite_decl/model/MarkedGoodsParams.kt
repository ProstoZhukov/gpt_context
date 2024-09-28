package ru.tensor.sbis.cashboxes_lite_decl.model

import ru.tensor.sbis.catalog_decl.catalog.NomenclatureType
import ru.tensor.sbis.catalog_decl.catalog.isMarked
import java.io.Serializable
import java.math.BigDecimal

/**
 * Класс для хранения информации о маркировке товара
 * @param barcodeContainer код маркировки или штрих-код, если товар немаркированный
 * @param markedGoodsType тип маркированной продукции [NomenclatureType]
 * @param minRetailPrice минимальная розничная цена (МРЦ)
 * @param maxRetailPrice максимальная розничная цена (МРЦ)
 * @param serialNumber серийный номер
 * @param ean EAN товара
 * @param milkWeight вес маркированной молочной продукции (сыра) в граммах
 * @param barcodeCheckStatus статус проверки кода маркировки
 */
data class MarkedGoodsParams(
    val barcodeContainer: String? = null,
    val markedGoodsType: NomenclatureType? = null,
    val minRetailPrice: BigDecimal? = null,
    val maxRetailPrice: BigDecimal? = null,
    val serialNumber: String? = null,
    val ean: String? = null,
    val milkWeight: BigDecimal? = null,
    val barcodeCheckStatus: Int? = null,
    val barcodeCheckStatusMessage: String? = null
) : Serializable {

    /**
     * Является ли [markedGoodsType] однозначным или лишь одним из возможных типов маркированной продукции
     */
    var isTypeAccurate = true

    /**
     * Варианты типов номенклатур, подходящие под данный код маркировки
     */
    var suitableMpGroups = emptyList<NomenclatureType>()

    /**
     * Код для поиска чеков в ОФД, передается на облако в качестве серийного номера.
     */
    var serialNumberForOfd: String? = null

    /** Является ли номенклатура блочной согласно информации из маркировки? */
    fun isBlockPackage() = markedGoodsType == NomenclatureType.TOBACCO_BLOCK

    /** Является ли номенклатура упаковкой НСП согласно информации из маркировки? */
    fun isNicotinePackage() = markedGoodsType == NomenclatureType.NICOTINE_CONTAINING_GROUP

    /** Является ли продукция маркированной? */
    fun isMarkedProduct() = markedGoodsType.isMarked()

    /** Является ли номенклатура мехом согласно информации из маркировки? */
    fun isFurs() = markedGoodsType == NomenclatureType.FURS

    /** Является ли номенклатура алкоголем согласно информации из маркировки? */
    fun isAlco() = markedGoodsType == NomenclatureType.ALCOHOL

    /** Является ли номенклатура духами согласно информации из маркировки? */
    fun isPerfume() = markedGoodsType == NomenclatureType.PERFUME

    /** Предоставить серийный номер для валидации/добавления в продажу или возврат */
    fun getSerialNumberForPosition() = if (!serialNumber.isNullOrBlank()) serialNumberForOfd else null

    /** Является ли номенклатура пивом согласно информации из маркировки? */
    fun isBeer() = markedGoodsType == NomenclatureType.BEER

    /** Может ли номенклатура быть пивом согласно информации из маркировки? */
    fun isBeerAsSuitableGroup() = suitableMpGroups.contains(NomenclatureType.BEER)

    /** Может ли номенклатура быть алкоголем согласно информации из маркировки? */
    fun isAlcoholAsSuitableGroup() = suitableMpGroups.contains(NomenclatureType.ALCOHOL)

    /**
     * Получена ли информация о маркировке посредством сканирования КМ?
     * Наличие серийника в информации о маркировке - признак того что был отсканирован КМ, т.к серийник может быть изьят
     * только из него.
     * Если серийника нет, значит сканировали EAN, или код другого типа.
     * */
    fun isMarkedInfoWithValidKMCode() = isMarkedProduct() && !serialNumber.isNullOrBlank()

    fun getShortMarkingCode(): String? {
        return serialNumberForOfd?.let { serialNumberForOfd ->
            val shortMarkingCodeLength = 4
            if (serialNumberForOfd.length > shortMarkingCodeLength) {
                val start = serialNumberForOfd.length - shortMarkingCodeLength
                val end = serialNumberForOfd.length
                "*${serialNumberForOfd.substring(start, end)}"
            } else {
                serialNumberForOfd
            }
        }
    }
}
