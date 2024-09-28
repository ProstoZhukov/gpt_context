package ru.tensor.sbis.scanner.contract

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import ru.tensor.sbis.edo_decl.scanner.ScannerResult
import ru.tensor.sbis.scanner.ScannerPlugin
import ru.tensor.sbis.scanner.ui.DocumentScannerActivity

/**
 * @author am.boldinov
 */
class ScannerFeatureImpl : ScannerFeature {

    private fun scannerSingletonComponent() = ScannerPlugin.singletonComponent

    override fun getScannerActivityIntent(context: Context, requestCode: String): Intent =
        DocumentScannerActivity.getActivityIntent(context, requestCode)
            .also { intent ->
                if (context == context.applicationContext) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

    override fun getResult(requestCode: String): ScannerResult? =
        scannerSingletonComponent().scannerEventManager.getResult(requestCode)

    override fun scannerResultObservable(requestCode: String): Observable<ScannerResult> =
        scannerSingletonComponent().scannerEventManager.scannerResultObservable(requestCode)
}
