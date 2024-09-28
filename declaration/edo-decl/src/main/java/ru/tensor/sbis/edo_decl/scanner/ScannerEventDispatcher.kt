package ru.tensor.sbis.edo_decl.scanner

/**
 * @author am.boldinov
 */
interface ScannerEventDispatcher {

    fun dispatchScannerResult(result: ScannerResult)
}