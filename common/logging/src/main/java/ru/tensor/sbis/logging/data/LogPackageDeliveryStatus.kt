package ru.tensor.sbis.logging.data

/**
 * Статусы доствки пакета логов.
 *
 * @author av.krymov
 */
enum class LogPackageDeliveryStatus {
    PREPARING,
    WAITING,
    INPROCESS,
    SENT,
    WIFIWAITING,
    INETWAITING
}