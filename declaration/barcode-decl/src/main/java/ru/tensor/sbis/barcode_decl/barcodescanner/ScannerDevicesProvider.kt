package ru.tensor.sbis.barcode_decl.barcodescanner

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для получения и изменения информации о наличии активных сканеров штрих-кодов
 */
interface ScannerDevicesProvider : Feature {

    var hasActiveScanners: Boolean
}