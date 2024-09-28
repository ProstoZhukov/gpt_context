package ru.tensor.sbis.retail_decl.devices

/**
 * Классы результатов проверки сканирования
 */
sealed class ScannerCheckResult(val deviceId: Long?) {
    class AllCodesChecked(deviceId: Long?) : ScannerCheckResult(deviceId)
    class NoMarkingCode(deviceId: Long?) : ScannerCheckResult(deviceId)
    class LinearCodeChecked(deviceId: Long?) : ScannerCheckResult(deviceId)
    class MarkingCodeChecked(deviceId: Long?) : ScannerCheckResult(deviceId)
    class NoCodesChecked(deviceId: Long?) : ScannerCheckResult(deviceId)
}

/**
 * Тип результата сканирования
 */
enum class ScannerCheckResultType {
    SCAN_SUCCESS,
    SCAN_ERROR
}