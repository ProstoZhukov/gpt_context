package ru.tensor.sbis.communicator.common.data.model

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Класс наследуется для того, чтобы проще было отследить места использования и не было конфликтов в даггере
 */
class NetworkAvailability : AtomicBoolean(true) {
    fun on(): Unit = set(true)
    fun off(): Unit = set(false)
}