package ru.tensor.sbis.barcode_decl.barcodereader

import androidx.annotation.MainThread

/**
 * Интерфейс контейнер для передачи событий
 */
@MainThread
interface BarcodeScanEventContainer {

    /**
     * Слушатель для получения отсканированного кода
     */
    var listener: Listener?

    /**
     * Слушатель для обработки нажатий на кнопки экрана сканирования
     */
    var viewEventListener: ViewEventListener?

    /**
     * Интерфейс для получения отсканированного кода
     */
    interface Listener {

        /**
         * Ивент о нахождении кода [barcode]
         */
        fun onBarcodeEvent(barcode: Barcode)
    }

    /**
     * Интерфейс для обработки нажатий на кнопки экрана сканирования
     */
    interface ViewEventListener {

        /**
         * Обработчик нажатия на кнопку закрытия экрана.
         */
        fun onCloseClick()

        /**
         * Обработчки нажатия на кнопку завершения сканирования при [BarcodeReaderParams.allowBurstScan] == `true`
         *
         * @see [BarcodeReaderFeature.createBarcodeReaderHostFragment]
         * @see [BarcodeReaderParams]
         */
        fun onFinishProcessClick() = Unit

        /**
         * Обработчик нажатия на кнопку ручного ввода чека при [BarcodeReaderParams.manualInputStrategy] == [ManualInputStrategy.CUSTOM]
         *
         * @return Выключить ли вспышку
         *
         * @see [BarcodeReaderFeature.createBarcodeReaderHostFragment]
         * @see [BarcodeReaderParams]
         */
        fun onCustomManualInputClick(): Boolean = true

        fun shotClick() = Unit
    }

    /**
     * Отправить ивент о нахождении кода [barcode]
     */
    fun sendBarcodeEvent(barcode: Barcode)

    /**
     * Отправить ивент о нахождении кода [barcodeValue] с типом [barcodeSymbology]
     */
    fun sendBarcodeEvent(barcodeValue: String, barcodeSymbology: BarcodeSymbology)
}

/**
 * Реализация BarcodeScanEventContainer
 */
class BarcodeScanEventContainerImpl : BarcodeScanEventContainer {
    override var listener: BarcodeScanEventContainer.Listener? = null

    override var viewEventListener: BarcodeScanEventContainer.ViewEventListener? = null

    override fun sendBarcodeEvent(barcode: Barcode) {
        listener?.onBarcodeEvent(barcode)
    }

    override fun sendBarcodeEvent(barcodeValue: String, barcodeSymbology: BarcodeSymbology) {
        listener?.onBarcodeEvent(Barcode(barcodeValue, barcodeSymbology))
    }
}