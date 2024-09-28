package ru.tensor.sbis.barcode_decl.barcodereader

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Created by pv.menshikov on 07.03.18.
 * Интерфейс-контракт модуля сканеров штрихкодов.
 */
interface BarcodeReaderFeature : Feature {

    companion object {
        const val RESULT_KEY_BARCODE = "BARCODE_KEY"
        const val RESULT_KEY_BARCODE_TYPE = "BARCODE_TYPE_KEY"
        const val RESULT_KEY_BARCODE_FILE = "BARCODE_FILE"
    }

    /**
     * @brief Создать фрагмент сканера ШК. Для корректной работы требуется обработка ивентов [BarcodeScanEventContainer.ViewEventListener]
     *
     * ВАЖНО: для правильной обработки нажатия на кнопку "назад" нужно делегировать метод activity.onBackPressed()
     * (наследуется от FragmentBackPress). Вложенные экраны (например, экран ручного ввода)
     * будут сами закрываться, если же вложенных экранов нет, будет вызван метод [BarcodeScanEventContainer.ViewEventListener.onCloseClick]
     *
     * @param params BarcodeReaderParams - параметры экрана
     *
     * @return Фрагмент сканера штрихкодов
     *
     * @sample [ru.tensor.sbis.barcodereader.view.BarcodeCaptureActivity]
     */
    fun createBarcodeReaderHostFragment(params: BarcodeReaderParams): Fragment

    /**
     * Реализация одиночного сканирования с возвратом штрихкода в
     * onActivityResult: 
     * [RESULT_KEY_BARCODE] to [String]
     * [RESULT_KEY_BARCODE_TYPE] to [String]
     *
     * @param context Контекст (например, запускающая activity)
     * @param params параметры сканера
     */
    fun createIntentBarcodeCaptureActivity(
        context: Context,
        params: BarcodeReaderParams = BarcodeReaderParams(ManualInputStrategy.NONE)
    ): Intent

    /**
     * Метод провайдер [BarcodeEventContainer]
     *
     * @param appContext реализация BarcodeReaderSingletonComponentHolder
     */
    @Deprecated(
        message = "BarcodeEventContainer устарел и вскоре перестанет поддерживаться. " +
                "Вместо него будет использоваться BarcodeScanEventContainer",
        replaceWith = ReplaceWith(
            expression = "getBarcodeScanEventContainer",
            imports = arrayOf(
                "ru.tensor.sbis.barcode_decl.barcodereader.BarcodeScanEventContainer",
                "ru.tensor.sbis.barcode_decl.barcodereader.Barcode"
            )
        )
    )
    fun getBarcodeEventContainer(appContext: Context): BarcodeEventContainer

    /**
     * Метод провайдер [BarcodeScanEventContainer]
     *
     * @param appContext реализация BarcodeReaderSingletonComponentHolder
     */
    fun getBarcodeScanEventContainer(appContext: Context): BarcodeScanEventContainer

    /**
     * * @brief Получить информацию о совместимости устройства
     *
     * @return Флаг совместимости устройства.
     * true - устройство поддерживается без ограничений,
     * false - устройство поддерживается с ограничениями, или полностью не совместимо
     */
    fun isDeviceCompatible(): Boolean
}