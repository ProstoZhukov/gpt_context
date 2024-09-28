package ru.tensor.sbis.cashboxes_lite_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

/**
 * Полный результат валидации серийного номера /
 * @param alreadyPresentedInSaleNomenclatureUUID - UUID номенклатуры, который уже присутствует в продаже.
 * Используется для одного случая, когда валидация провалилась по причине того, что пользователь пытается добавить в продажу
 * товар через повтороное сканирование уже отсканенного КМ. В поле лежит UUID проблемной номенклатуры.
 * */
@Parcelize
data class ValidateProductCodeResult(
    val validatedProductCode: String,
    val validationResult: ValidateSerialState,
    val productCodeType: ProductCodeType,
    val nomenclatureName: String?,
    val operationDateTime: Date?,
    val alreadyPresentedInOperationNomenclatureUUID: UUID? = null,
    val additionalErrorMessage: String
) : Parcelable