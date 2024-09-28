package ru.tensor.sbis.design.retail_models

import java.math.BigDecimal

/**
 * Класс для хранения данных блока СНО в окне продажи
 * @property taxationSystemData список данных по СНО
 */
data class TaxationSystemBlockViewModel(val taxationSystemData: List<SaleTaxationSystemData>) {

    companion object {
        /** Поддерживаемый размер данных о системах налогообложения. */
        const val SUPPORTED_TAX_DATA_COUNT = 2
    }
}

/**
 * Данные о системе налогообложения, привязанные к данной продаже
 * @property taxSystemName короткое имя системы налогообложения
 * @property taxSystemCode кодовое обозначение системы налогообложения
 * @property sum сумма по указанной СНО
 */
data class SaleTaxationSystemData(
    val taxSystemName: String,
    val taxSystemCode: UiTaxSystemCode,
    val sum: BigDecimal
) {
    /** Является ли текущий [taxSystemCode] == [UiTaxSystemCode.PATENT]. */
    val isPatentCode: Boolean
        get() = taxSystemCode == UiTaxSystemCode.PATENT
}