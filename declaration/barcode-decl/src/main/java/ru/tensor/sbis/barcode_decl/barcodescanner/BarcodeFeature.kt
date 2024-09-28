package ru.tensor.sbis.barcode_decl.barcodescanner

import android.app.Dialog
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import io.reactivex.Observable
import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderFeature
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Общий контракт для barcode - reader фичи.
 *
 * @author aa.mezencev
 */
interface BarcodeFeature : Feature {

    /**
     * Фича barcode - reader'a.
     */
    val barcodeReaderFeature: BarcodeReaderFeature?

    /**
     * Есть ли камера у устройства?
     */
    val hasCamera: Boolean

    /**
     * Поставщик кодов.
     */
    fun barcodeObservable(callbackId: String? = null): Observable<String> = Observable.never()

    /**
     * Поставщик состояния активного hardware - девайса.
     */
    fun hasActiveHardwareDevice(): Observable<Boolean> = Observable.just(false)

    /**
     * Получить обработчик нажатий с клавиатурного сканнера для [DialogFragment].
     */
    fun Dialog.getScannerKeyboardEventHandler(): ScannerKeyEventHandler? = null

    /** @SelfDocumented **/
    fun interface ScannerKeyEventHandler {

        /** @SelfDocumented **/
        fun setKeyEvent(keyEvent: KeyEvent?): Boolean
    }
}