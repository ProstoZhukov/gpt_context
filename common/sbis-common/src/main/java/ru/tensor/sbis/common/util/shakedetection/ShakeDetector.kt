package ru.tensor.sbis.common.util.shakedetection

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Вспомогательный класс для создания [Observable] из сенсора.
 *
 * @author ar.leschev
 */
class ShakeDetector {

    /**
     * Создает [Observable] и излучает события, когда произошло [SHAKES_COUNT] взмахов за [SHAKES_PERIOD] секунд.
     */
    fun create(context: Context): Observable<Unit> = createAccelerationObservable(context)
        .filter { abs(it.values[0]) > THRESHOLD }
        .map { XEvent(it.timestamp, it.values[0]) }
        .buffer(BUFFER_SIZE, 1)
        .filter { buffer -> buffer[0].x * buffer[1].x < 0 }
        .map { buffer -> buffer[1].timestamp.convertToSeconds() }
        .buffer(SHAKES_COUNT, 1)
        .filter { buffer -> buffer[SHAKES_COUNT - 1] - buffer[0] < SHAKES_PERIOD }
        .map { } //Результат не нужен, нужно подать только сигнал
        .throttleFirst(SHAKES_PERIOD, TimeUnit.SECONDS)

    private fun createAccelerationObservable(context: Context): Observable<SensorEvent> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION)

        return if (sensorList.isNotEmpty()) {
            SensorObservableFactory.createSensorEventObservable(sensorList.first(), sensorManager)
        } else {
            Timber.d("Device has no linear acceleration sensor")
            Observable.never()
        }
    }

    /** Преобразовать наносек в сек. */
    private fun Long.convertToSeconds() = this / SEC_DIVIDER

    private companion object {
        /**
         * Чувствительность ускорения, чем больше, тем сильнее нужно трясти телефон.
         */
        private const val THRESHOLD = 13

        /**
         * Количество взмахов за период [SHAKES_PERIOD].
         */
        private const val SHAKES_COUNT = 3

        /**
         * Количество секунд за которое нужно успеть сделать [SHAKES_COUNT] взмахов.
         */
        private const val SHAKES_PERIOD = 1L

        private const val BUFFER_SIZE = 2
        private const val SEC_DIVIDER = 1_000_000_000f
    }
}