package ru.tensor.sbis.design.media_player.helpers

import android.content.Context
import android.os.PowerManager
import timber.log.Timber

/**
 * Хелпер для разблокировки экрана.
 *
 * @author da.zhukov
 */
internal class WakeLockHelper(appContext: Context) {

    private val powerManager by lazy {
        appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private var wakeLock: PowerManager.WakeLock? = null

    /**@SelfDocumented*/
    @Suppress("DEPRECATION")
    fun requestWakeLock(request: Boolean) {
        try {
            if (request) {
                cancelWakeLock()
                wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    MEDIA_PLAYER_WAKELOCK_TAG
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

private const val MEDIA_PLAYER_WAKELOCK_TAG = "ru.tensor.sbis:MEDIA_PLAYER"
private const val WAKELOCK_TIMEOUT_MS = 5 * 60 * 1000L