package ru.tensor.sbis.catalog_decl.catalog

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Контракт для класса обработки получения кодов.
 *
 * @author aa.mezencev
 */
interface BarcodeReceiveHandler : Feature {

    /**
     * Получен код
     */
    fun receiveBarcode()
}