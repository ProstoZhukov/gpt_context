package ru.tensor.sbis.scanner.data

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.edo_decl.scanner.ScannerEventDispatcher
import ru.tensor.sbis.edo_decl.scanner.ScannerEventProvider
import ru.tensor.sbis.edo_decl.scanner.ScannerResult

/**
 * @author am.boldinov
 */
class ScannerEventManager : ScannerEventDispatcher, ScannerEventProvider {

    private val resultSubject: PublishSubject<ScannerResult> = PublishSubject.create()
    private val results: MutableList<ScannerResult> = mutableListOf()

    @Synchronized
    private fun addResult(result: ScannerResult) {
        results.add(result)
    }

    @Synchronized
    private fun removeResult(requestCode: String): ScannerResult? {
        val resultsIterator = results.listIterator()
        while (resultsIterator.hasNext()) {
            val result = resultsIterator.next()
            if (result.requestCode == requestCode) {
                resultsIterator.remove()
                return result
            }
        }
        return null
    }

    //region ScannerEventDispatcher
    override fun dispatchScannerResult(result: ScannerResult) {
        addResult(result)
        resultSubject.onNext(result)
    }
    //endregion

    //region ScannerEventProvider
    override fun scannerResultObservable(requestCode: String): Observable<ScannerResult> =
        removeResult(requestCode).let { resultByRequestCode ->
            resultSubject
                .share()
                .filter { it.requestCode == requestCode }
                .let { scannerResultObservable ->
                    if (resultByRequestCode != null) {
                        scannerResultObservable.startWith(resultByRequestCode)
                    } else {
                        scannerResultObservable.doOnNext { removeResult(requestCode) }
                    }
                }
        }

    override fun getResult(requestCode: String): ScannerResult? =
        removeResult(requestCode)
    //endregion
}