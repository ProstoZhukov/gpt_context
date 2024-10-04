package ru.tensor.sbis.design.message_panel.recorder_common.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.PowerManager
import timber.log.Timber

/**
 * Вспомогательный класс для управления девайсом в процессе записи аудио/видео.
 *
 * @author vv.chekurda
 */
class RecordingDeviceHelper(private val activity: Activity) {

    private val systemAudioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager

    private var wakeLock: PowerManager.WakeLock? = null
    private var hasRecordAudioFocus = false
    private val audioRecordFocusChangedListener = AudioManager.OnAudioFocusChangeListener { focus ->
        if (focus != AudioManager.AUDIOFOCUS_GAIN) hasRecordAudioFocus = false
    }

    private var isConfigured: Boolean = false

    /**
     * Включение/выключение блокировки ориентации экрана при настройке девайса [configureDevice] для записи.
     */
    var isLockOrientationEnabled: Boolean = true

    /**
     * Включение/выключение блокировки погасания экрана при настройке девайса [configureDevice] для записи.
     */
    var isWakeLockEnabled: Boolean = true
        get() = wakeLockTimeout > 0L && field

    /**
     * Таймаут автоматического выключения блокировки погасания экрана.
     * @see isWakeLockEnabled
     */
    var wakeLockTimeout = WAKELOCK_TIMEOUT_MS

    /**
     * Настроить девайс для записи аудио/видео.
     * В настройке участвуют следующие параметры:
     * - Блокируется ориентация экрана.
     * - Производится попытка переключения на блютуз аудио-вход.
     * - Останавливаются текущие проигрываемые аудио/видео на девайсе.
     * - Блокируется погасание экрана.
     *
     * @param isStartRecording true, если необходима настройка перед записью,
     * в ином случае настройки вернутся к прежним параметрам.
     */
    fun configureDevice(isStartRecording: Boolean) {
        if (isConfigured && isStartRecording || !(isConfigured || isStartRecording)) return
        isConfigured = isStartRecording
        requestLockOrientation(isStartRecording)
        requestBluetoothSco(isStartRecording)
        requestAudioFocus(isStartRecording)
        requestWakeLock(isStartRecording)
    }

    /**
     * Запрос на блокировку смены ориентации девайса.
     */
    fun requestLockOrientation(request: Boolean) {
        if (!isLockOrientationEnabled) return
        activity.requestedOrientation = if (request) {
            ActivityInfo.SCREEN_ORIENTATION_LOCKED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun requestBluetoothSco(request: Boolean) = with(systemAudioManager) {
        if (request == isBluetoothScoOn) return
        if (request) {
            try {
                startBluetoothSco()
                isBluetoothScoOn = request
            } catch (e: RuntimeException) {
                Timber.e(e)
            }
        } else {
            stopBluetoothSco()
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(request: Boolean) {
        if (request && !hasRecordAudioFocus) {
            systemAudioManager.requestAudioFocus(
                audioRecordFocusChangedListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            hasRecordAudioFocus = true
        } else if (!request && hasRecordAudioFocus) {
            systemAudioManager.abandonAudioFocus(audioRecordFocusChangedListener)
            hasRecordAudioFocus = false
        }
    }

    private fun requestWakeLock(request: Boolean) {
        try {
            if (request) {
                cancelWakeLock()
                wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    AUDIO_RECORD_WAKELOCK_TAG
                ).also {
                    it.setReferenceCounted(false)
                    it.acquire(WAKELOCK_TIMEOUT_MS)
                }
            } else {
                cancelWakeLock()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun cancelWakeLock() {
        wakeLock?.also {
            it.release()
            wakeLock = null
        }
    }
}

private const val WAKELOCK_TIMEOUT_MS = 3 * 60 * 1000L
private const val AUDIO_RECORD_WAKELOCK_TAG = "ru.tensor.sbis:AUDIO_RECORD_WAKELOCK"