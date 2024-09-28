package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private const val DEFAULT_VALUE_DATABITS = 8
private const val DEFAULT_VALUE_STOPBITS = 1

/**
 * Модель с информацией о подключении устройства
 *
 * @param driver String - драйвер устройства
 * @param connectionType ConnectionTypeInside - тип подключения устройства
 * @param serialPort String? - порт через который подключено устройство (опционально)
 * @param prefix String - префикс сканера
 * @param suffix String - суффикс сканера
 * @param timeoutMs: Int? - таймаут сканера (опционально)
 * @param deviceName String? - имя устройства (опционально)
 * @param macAddress String? - мак адресс устройства (опционально)
 * @param identifier String? - идентификатор устройства (опционально)
 * @param ipAddress String? - айпи адрес если TCP/IP подключение (опционально)
 * @param tcpPort Int? - порт если TCP/IP подключение (опционально)
 * @param login String? - логин (опционально)
 * @param password String? - пароль (опционально)
 * @param baudrate - скорость передачи данных по последовательному порту в бит/с
 * @param dataBits - количество бит данных в посылке
 * @param parity - тип используемой проверки чётности
 * @param stopBits - количество стоп-бит в посылке
 *
 * @see ConnectionTypeInside
 */
@Parcelize
data class DeviceConnectionInside(val driver: String,
                                  val driverDirectory: String? = null,
                                  val connectionType: ConnectionTypeInside,
                                  val serialPort: String?,
                                  val prefix: String = "",
                                  val suffix: String = "",
                                  val timeoutMs: Int? = null,
                                  val usbDeviceId: UsbDeviceId = UsbDeviceId(),
                                  val deviceName: String? = null,
                                  val macAddress: String = "",
                                  val identifier: String? = "0",
                                  val ipAddress: String? = null,
                                  val tcpPort: Int? = null,
                                  val login: String = "",
                                  val password: String? = null,
                                  val baudrate: Int? = null,
                                  val dataBits: Int = DEFAULT_VALUE_DATABITS,
                                  val parity: Int = 0,
                                  val stopBits: Int = DEFAULT_VALUE_STOPBITS,
                                  val additionalSettings: String? = null,
                                  val countryCode: Int) : Parcelable