package ru.tensor.sbis.barcode_decl.barcodereader

import android.content.pm.ActivityInfo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры экрана сканирования
 *
 * @param manualInputStrategy Режим работы кнопки ручного ввода
 * @param allowBurstScan Поддержка множественного сканирования. Добавляет кнопку завершить.
 * Обработка клика: [BarcodeScanEventContainer.ViewEventListener.onFinishProcessClick]
 * @param settingButtonVisibleType Видимость кнопки настроек
 * @param autoFlashParams параметры автовспышки
 * @param startDelay Задержка на включение камеры и старта процесса распознавания
 * @param isSaveManualFlashStateAfterRestore флаг на принудительное сохранение вспышки для следующего открытия экрана.
 * Будет использоваться только для ручного управления вспышкой
 * @param topMessage информационное сообщение под тулбаром
 * @param toolbarMessage информационное сообщение в тулбаре
 * @param galleryIconVisible видно ли иконку галереи
 * @param flashIconVisible видно ли иконку вспышки
 * @param needSetBarcodeDoneButtonGoneMargin нужно ли устанавливать goneMarginBottom для кнопки finish
 * @param needBarcodeProcess нужно ли обрабатывать баркод, вычищать неожиданные последовательности в коде и проверять на наличие непечатаемых символов
 * @param screenOrientation ориентация экрана
 * @param needTimeoutAfterEachBarcode нужно ли ставить задержку после каждого остканированного баркода
 * @param needShowQr нужно ли показывать подложку для сканирования QR-кода
 * @param referenceURL URL для перехода в справочник
 */
@Parcelize
data class BarcodeReaderParams(
    val manualInputStrategy: ManualInputStrategy,
    val allowBurstScan: Boolean = false,
    val settingButtonVisibleType: SettingButtonVisibleType = SettingButtonVisibleType.VISIBLE_ONLY_DEBUG,
    val autoFlashParams: AutoFlashParams = AutoFlashParams(),
    val startDelay: Long = 0,
    val isSaveManualFlashStateAfterRestore: Boolean = false,
    val topMessage: String? = null,
    val toolbarMessage: String? = null,
    val galleryIconVisible: Boolean = true,
    val flashIconVisible: Boolean = true,
    val needSetBarcodeDoneButtonGoneMargin: Boolean = true,
    val needBarcodeProcess: Boolean = true,
    val screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
    val needTimeoutAfterEachBarcode: Boolean = false,
    val needShowQr: Boolean = false,
    val needShowQrAndShot: Boolean = false,
    val referenceURL: String? = null
) : Parcelable

/**
 * Перечисление типов обработчкиков для кнопки ручного ввода данных
 */
@Parcelize
enum class ManualInputStrategy : Parcelable {

    /**
     * Откроется экран ручного ввода чека
     *
     * ВАЖНО: для правильной обработки нажатия на кнопку "назад" нужно делегировать метод activity.onBackPressed()
     * для DefaultScannerFragment (наследуется от FragmentBackPress). Вложенные экраны (например, экран ручного ввода)
     * будут сами закрываться и метод FragmentBackPress.onBackPressed() будет возвращать `true`, если же вложенных
     * экранов нет, метод вернет `false`
     */
    RECEIPT,

    /**
     * Кастомный обработчик
     *
     * Обработка клика:
     * @see [BarcodeScanEventContainer.ViewEventListener.onCustomManualInputClick]
     */
    CUSTOM,

    /**
     * Кнопка не будет видна
     */
    NONE
}

/**
 * Видимость кнопки настроек
 */
enum class SettingButtonVisibleType {

    /**
     * Кнопка видима всегда
     */
    VISIBLE,

    /**
     * Кнопка не будет видна
     */
    INVISIBLE,

    /**
     * Кнопка видима только для debug сборок
     */
    VISIBLE_ONLY_DEBUG
}

/**
 * Параметры автовспышки
 *
 * @param isEnabled Вкл/выкл выспышка
 * @param maxLumaToOnFlash Максимальный порог для включения вспышки [0, 1]
 * @param delayToOff Задержка на выключение вспышки
 * @param delayToOn Задержка на включение вспышки
 * @param flashAnalysisFramesCount Необходимое кол-во кадров для подтверждения состояния вспышки.
 * При малом fps и большом значении значительно возрастут задержки
 * @param isUsingCentering Для определения яркости используется центральная часть preview
 */
@Parcelize
data class AutoFlashParams(
    var isEnabled: Boolean = false,
    val maxLumaToOnFlash: Double = 0.25,
    val delayToOff: Long = 3500L,
    val delayToOn: Long = 80L,
    val flashAnalysisFramesCount: Long = 3,
    val isUsingCentering: Boolean = false
) : Parcelable