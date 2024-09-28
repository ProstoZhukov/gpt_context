package ru.tensor.sbis.communication_decl.communicator.media

import android.hardware.SensorEventListener

/**
 * Хелпер содержит логику переключения динамиков во время проигрывания медиа (см. [MediaPlayer])
 * в зависимости от того поднес пользователь телефон к уху или нет.
 *
 * @author da.zhukov
 */
interface ProximityHelper : SensorEventListener, MediaPlayer.PlayingStateListener {

    /**
     * Начать отслеживание датчиком.
     */
    fun start()

    /**
     * Остановить отслеживание датчиком.
     */
    fun stop()
}