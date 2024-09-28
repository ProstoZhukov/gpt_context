package ru.tensor.sbis.design.media_player.proximity

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.*
import android.media.AudioManager
import android.media.AudioManager.MODE_IN_COMMUNICATION
import android.media.AudioManager.MODE_NORMAL
import android.os.PowerManager
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.ProximityHelper
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import timber.log.Timber

/**
 * Реализация [ProximityHelper].
 */
class ProximityHelperImpl(
    private val mediaPlayer: MediaPlayer,
    private val appContext: Context
) : ProximityHelper {

    private val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var wakeLock: PowerManager.WakeLock? = null

    private var sensorManager: SensorManager? = null
    private var proximitySensor: Sensor? = null
    private var audioManager: AudioManager? = null

    private var isPlayingActive = false
    private var isNear = false

    private var useFrontSpeaker = false

    /**
     * Начать отслеживание датчиком.
     */
    override fun start() {
        if (sensorManager != null) return
        isPlayingActive = mediaPlayer.isPlayingActive()
        mediaPlayer.setPlayingListener(this)
        sensorManager = appContext.getSystemService(SENSOR_SERVICE) as? SensorManager ?: return
        proximitySensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return
        audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
    }

    /**
     * Остановить отслеживание датчиком.
     */
    override fun stop() {
        mediaPlayer.setPlayingListener(null)
        sensorManager?.unregisterListener(this, proximitySensor)
        sensorManager = null
        proximitySensor = null
        audioManager?.let { if (it.mode == MODE_IN_COMMUNICATION) it.mode = MODE_NORMAL }
        audioManager = null
        requestWakeLock(request = false)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PROXIMITY) return
        isNear = event.values[0] == 0f
        update()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun playingStateChanged(isActive: Boolean) {
        isPlayingActive = isActive
        sensorManager?.also { sensor ->
            if (isActive) {
                sensor.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            } else {
                sensor.unregisterListener(this, proximitySensor)
            }
        }
        update()
    }

    private fun update() {
        if (isNear && isPlayingActive) {
            onNear()
            requestWakeLock(request = true)
        } else {
            onAway()
            requestWakeLock(request = false)
        }
    }

    private fun onNear() {
        val manager = audioManager ?: return
        if (!useFrontSpeaker) {
            useFrontSpeaker = true
            manager.isBluetoothScoOn = false
            manager.isSpeakerphoneOn = false

            mediaPlayer.getMediaInfo()?.let {
                mediaPlayer.changeAudioRoute(useFrontSpeaker)
                if (it.state == State.PAUSED) {
                    mediaPlayer.play()
                }
            }
        }
    }

    private fun onAway() {
        val manager = audioManager ?: return
        if (useFrontSpeaker) {
            useFrontSpeaker = false
            manager.isSpeakerphoneOn = true

            mediaPlayer.getMediaInfo()?.let {
                mediaPlayer.changeAudioRoute(useFrontSpeaker)
                if (it.state == State.PLAYING) {
                    mediaPlayer.pause()
                }
            }
        }
    }

    private fun requestWakeLock(request: Boolean) {
        try {
            if (request) {
                cancelWakeLock()
                wakeLock = powerManager.newWakeLock(
                    PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    PROXIMITY_WAKELOCK_TAG
                ).also {
                    it.setReferenceCounted(false)
                    it.acquire(WAKE_LOCK_TIMEOUT_MS)
                }
            } else {
                cancelWakeLock()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun cancelWakeLock() {
        wakeLock?.release()
        wakeLock = null
    }
}

private const val WAKE_LOCK_TIMEOUT_MS = 30_000L
private const val PROXIMITY_WAKELOCK_TAG = "ru.tensor.sbis:PROXIMITY_WAKELOCK"