package ru.tensor.sbis.barcode_decl.barcodereader

import androidx.annotation.MainThread

/**
 * Интерфейс контейнер для передачи событий
 */
@MainThread
interface BarcodeEventContainer {

    /**
     * Слушатель для получения отсканированного кода
     */
    var listener: Listener?

    /**
     * Интерфейс для получения отсканированного кода
     */
    interface Listener {

        /**
         * Ивент о нахождении кода [barcode] без типа.
         * Для получения кодов с их типом нужно использовать [BarcodeScanEventContainer.Listener]
         */
        fun onBarcodeEvent(barcode: String)
    }

    /**
     * Отправить ивент о нахождении кода [barcode] без типа
     * Для отправки с типом нужно использовать [BarcodeScanEventContainer.sendBarcodeFileEvent]
     */
    fun sendBarcodeEvent(barcode: String)
}

/**
 * Реализация BarcodeEventContainer
 */
class BarcodeEventContainerImpl : BarcodeEventContainer {

    override var listener: BarcodeEventContainer.Listener? = null

    override fun sendBarcodeEvent(barcode: String) {
        listener?.onBarcodeEvent(barcode)
    }
}