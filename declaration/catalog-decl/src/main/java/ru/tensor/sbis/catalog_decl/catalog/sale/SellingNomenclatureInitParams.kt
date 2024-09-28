package ru.tensor.sbis.catalog_decl.catalog.sale

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.catalog_decl.catalog.OperationType
import java.util.UUID

/**
 *  Параметры для инициализации карточки в продаже
 */
@Parcelize
class SellingNomenclatureInitParams(
    val operationUUID: UUID,
    val saleNomenclatureUUID: UUID,
    val catalogUUID: UUID,
    val priceEditable: Boolean,
    val operationType: OperationType,
    val isMarkedCodeAvailable: Boolean
) : Parcelable