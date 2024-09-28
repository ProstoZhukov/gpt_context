package ru.tensor.sbis.edo_decl.scanner

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * @author am.boldinov
 */
interface ScannerEventProvider: Feature {

    fun scannerResultObservable(requestCode: String): Observable<ScannerResult>

    fun getResult(requestCode: String): ScannerResult?
}