package ru.tensor.sbis.common.util.shakedetection

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Observable

/**
 * Фабрика для создания потока событий из сенсора.
 *
 *  @author ar.leschev
 */
internal object SensorObservableFactory {

    /**
     * Создать поток событий из [sensor].
     */
    fun createSensorEventObservable(
        sensor: Sensor,
        sensorManager: SensorManager
    ): Observable<SensorEvent> = Observable.create { emitter ->
        val listener: SensorEventListener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                if (!emitter.isDisposed) emitter.onNext(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        emitter.setCancellable {
            sensorManager.unregisterListener(listener)
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

}