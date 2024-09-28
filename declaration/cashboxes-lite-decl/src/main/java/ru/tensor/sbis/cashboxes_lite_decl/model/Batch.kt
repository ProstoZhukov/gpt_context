package ru.tensor.sbis.cashboxes_lite_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

/**
 * Модель партии
 *
 * @property batchId Идентификатор партии
 * @property nomenclatureUuid Идентификатор номенклатуры
 * @property supplier Поставщик партии
 * @property series Серия партии
 * @property code Актуальный код партии
 * @property quantity Остаток по партии
 * @property price Цена за единицу внутри партии
 * @property receiptDate Дата и время поставки
 * @property expirationDate Дата и время истечения срока годности
 */
@Parcelize
data class Batch(
    val batchId: Long,
    val nomenclatureUuid: UUID,
    val supplier: String,
    val series: String,
    val code: String,
    val quantity: BigDecimal?,
    val price: BigDecimal?,
    val receiptDate: Date?,
    val expirationDate: Date?,
    val isMarkedDrugs: Boolean?
) : Parcelable, Serializable

/**
 * Виды индикации срока годности партии
 */
enum class BatchExpiryDateIndication {
    NONE,
    EXPIRED,
    WARNING
}

/**
 * Класс-агрегатор [Batch] и [BatchExpiryDateIndication]
 *
 * @property batch см. [Batch]
 * @property expiryDateIndication см. [BatchExpiryDateIndication]
 */
@Parcelize
data class IndicatedBatch(val batch: Batch, val expiryDateIndication: BatchExpiryDateIndication) : Parcelable