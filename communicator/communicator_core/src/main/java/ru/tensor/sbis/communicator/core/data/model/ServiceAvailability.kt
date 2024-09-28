package ru.tensor.sbis.communicator.core.data.model

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Класс для хранения значения об изменении текущего состояния сети
 */
class ServiceAvailability : AtomicBoolean(true) {

        /** Сеть появилась */
        fun on(): Unit = set(true)

        /** Сеть пропала */
        fun off(): Unit = set(false)
}