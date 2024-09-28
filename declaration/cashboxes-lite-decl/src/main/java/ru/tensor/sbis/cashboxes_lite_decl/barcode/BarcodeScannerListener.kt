package ru.tensor.sbis.cashboxes_lite_decl.barcode

import io.reactivex.Completable

/** Слушатель событий связанных с работой с баркодом */
interface BarcodeScannerListener {

    /**
     *  Добавляет callback с заданным идентификатором в очередь callback - ов класса ScannerLifecycleManager
     *  При сканировании баркода, выполяется последний callback из очереди.
     */
    fun addBarcodeCallBack(callback: (barcode: String) -> Unit, callbackId: String?)

    /** Удаляет callback c заданным идентификатором из очереди callback - ов  */
    fun removeFromBarcodeCallBackList(callbackId: String? = null)

    /** Перезагрузка сканеров, подключенных к текущему рабочему месту  */
    fun reloadScannersAsync(): Completable

    /** Остановить проверку соединения */
    fun stopCheckConnection()
}