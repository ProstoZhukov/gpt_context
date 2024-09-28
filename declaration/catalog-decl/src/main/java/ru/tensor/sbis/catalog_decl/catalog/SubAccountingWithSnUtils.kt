package ru.tensor.sbis.catalog_decl.catalog

/**
 * Проверка есть ли у подвида учета серийный номер
 */
fun isSubAccountingWithSn(subAccounting: SubAccounting?) =
    when(subAccounting) {
        SubAccounting.JEWELLERY -> true
        else -> false
    }