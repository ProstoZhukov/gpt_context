package ru.tensor.sbis.business.common.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.R
import timber.log.Timber
import javax.inject.Inject

/**
 * Класс-поставщик шрифтов в приложение, оболочка над ApplicationContext
 */
open class FontProvider @Inject constructor(val context: Context) {

    fun requestRobotoRegularFont(
        onRetrieved: (Typeface) -> Unit,
        onFontFailed: (Int?) -> Unit = {}
    ) = requestFont(R.font.roboto_regular, onRetrieved, onFontFailed)

    fun requestRobotoMediumFont(
        onRetrieved: (Typeface) -> Unit,
        onFontFailed: (Int?) -> Unit = {}
    ) = requestFont(R.font.roboto_medium, onRetrieved, onFontFailed)

    fun requestRobotoBoldFont(
        onRetrieved: (Typeface) -> Unit,
        onFontFailed: (Int?) -> Unit = {}
    ) = requestFont(R.font.roboto_bold, onRetrieved, onFontFailed)

    private fun requestFont(
        @FontRes fontResId: Int,
        onRetrieved: (Typeface) -> Unit,
        onFontFailed: (Int?) -> Unit = {}
    ) {
        val handler: Handler? = getFontHandler()
        try {
            ResourcesCompat.getFont(context.applicationContext,
                                    fontResId,
                                    object : ResourcesCompat.FontCallback() {
                                        override fun onFontRetrieved(typeface: Typeface) {
                                            handler?.let { stopFontHandler(it) }
                                            onRetrieved(typeface)
                                        }

                                        override fun onFontRetrievalFailed(reason: Int) {
                                            handler?.let { stopFontHandler(it) }
                                            onFontFailed(reason)
                                        }
                                    },
                                    handler)
        } catch (e: Exception) {
            handler?.let { stopFontHandler(it) }
            onFontFailed(0)
        }
    }

    private fun getFontHandler(): Handler {
        val handlerThread = HandlerThread("FontBackground")
        handlerThread.start()
        return Handler(handlerThread.looper)
    }

    private fun stopFontHandler(handler: Handler) {
        try {
            val looper = handler.looper
            if (looper != null) {
                looper.quitSafely()
                if (!looper.thread.isInterrupted) {
                    looper.thread.interrupt()
                }
            }
        } catch (e: InterruptedException) {
            Timber.e(e)
        }
    }
}