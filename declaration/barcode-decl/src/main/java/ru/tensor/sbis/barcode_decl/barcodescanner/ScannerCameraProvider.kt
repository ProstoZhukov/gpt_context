package ru.tensor.sbis.barcode_decl.barcodescanner

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для получения информации о наличии камеры.
 */
interface ScannerCameraProvider : Feature {

    val hasCamera: Boolean
}