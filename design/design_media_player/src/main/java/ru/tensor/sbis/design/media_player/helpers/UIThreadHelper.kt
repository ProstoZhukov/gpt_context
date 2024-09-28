package ru.tensor.sbis.design.media_player.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import ru.tensor.sbis.mediaplayer.BuildConfig
import timber.log.Timber

/**
 * Хелпер для работы с UI потоком.
 *
 * @author da.zhukov
 */
internal class UIThreadHelper(appContext: Context) {

    private val mainThreadHandler = Handler(appContext.mainLooper)

    /**@SelfDocumented*/
    fun ensureMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val ensureMainThreadException = RuntimeException("This method should be called on main thread")
            if (BuildConfig.DEBUG) {
                throw ensureMainThreadException
            } else {
                Timber.e(ensureMainThreadException)
            }
        }
    }

    /**@SelfDocumented*/
    fun runOnUIThread(block: () -> Unit) {
        mainThreadHandler.post(block)
    }
}