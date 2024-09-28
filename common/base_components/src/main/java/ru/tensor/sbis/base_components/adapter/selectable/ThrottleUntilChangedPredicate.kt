package ru.tensor.sbis.base_components.adapter.selectable

import android.os.SystemClock
import io.reactivex.functions.Predicate

/**
 * Предикат, ограничивающий поток данных в зависимости от интервала времени.
 */
/**
 * Класс используется для определения того, должен ли элемент быть выпущен на основе длительности дросселя.
 *
 * @param throttleDuration Продолжительность дросселирования, в миллисекундах. Значение по умолчанию - [EMIT_THROTTLE_DURATION].
 * @param shouldThrottle Следует ли применять дроссель.
 * @param timeStampProvider Функция, предоставляющая текущую метку времени. Значение по умолчанию - SystemClock.elapsedRealtime().
 */
class ThrottleUntilChangedPredicate<ITEM_TYPE : Any>(
    val throttleDuration: Long = EMIT_THROTTLE_DURATION,
    var shouldThrottle: Boolean = false,
    val timeStampProvider: () -> Long = { SystemClock.elapsedRealtime() }
) : Predicate<ITEM_TYPE> {

    /**
     * Временная метка предыдущего испускания.
     */
    var previousStamp: Long = 0
    /**
     * Следует ли игнорировать выбор при дросселировании.
     */
    internal var ignoreSelectionThrottling = false

    /**
     * Проверяет текущий элемент [current], чтобы определить, должен ли он быть испущен.
     */
    override fun test(current: ITEM_TYPE): Boolean {
        if (!shouldThrottle) return true

        return if (ignoreSelectionThrottling) {
            previousStamp = 0
            true
        } else {
            val currentStamp = timeStampProvider()
            //не пропускаем эмит, если это повторный эмит в указанном интервале времени
            val throttle = currentStamp < previousStamp + throttleDuration
            previousStamp = currentStamp
            !throttle
        }
    }
}

/**
 * Длительность дросселирования по умолчанию, в миллисекундах.
 */
private const val EMIT_THROTTLE_DURATION = 800L