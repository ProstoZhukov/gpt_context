package ru.tensor.sbis.crud.devices.settings.model

/**
 * Перечисление типов подключения устройств
 */

enum class ConnectionTypeInside(private val num: Int) {
    SERIAL_PORT(0),
    TCP_IP(1),
    USB_HID(2),
    KEYBOARD(3),
    BLUETOOTH(7),
    PAYMENT_TERMINAL_OVER_LIB(10),
    USB_SERIAL_CDC_ACM(15),
    INPAS_DUAL_CONNECTOR(16),
    NFC(17),
    VENDOR_SPECIFIC(19),
    USB_SERIAL_OVER_ADAPTER(20),
    EFTBASE(21),
    USB_PRINTER(22),
    WEBKASSA(23),
    EKASSA(24),
    EKASSA_ONLINE(25);

    /**
     * Получения номера типа подключения Int
     */
    fun toInt(): Int = num
}