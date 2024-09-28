package ru.tensor.sbis.webviewer.utils

import android.app.Application.getProcessName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.WebView
import ru.tensor.sbis.common.util.DeviceUtils.isEmulator
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver

/**
 * Broadcast Receiver - для обработки широковещательных сообщений при тестирование
 *
 * Пример вызова:
 * adb shell "am broadcast -a ru.tensor.sbis.docwebviewer.WEB_VIEW_DEBUG_BROADCAST" --es ENABLE_WEB_VIEW_DEBUG true
 *
 * @author ma.kolpakov
 */
class WebViewDebugBroadcastReceiver : EntryPointBroadcastReceiver() {

    override fun onReady(context: Context, intent: Intent) {
        if (intent.action == DOC_WEB_VIEW_BROADCAST_ACTION && isEmulator()) {
            val textValue = intent.getStringExtra(ENABLE_WEB_VIEW_DEBUG).orEmpty()
            val value = textValue.toBoolean()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val processName: String = getProcessName()
                if (processName.contains(APP_METRICA_PROCESS_SUFFIX)) return
            }
            WebView.setWebContentsDebuggingEnabled(value)
        }
    }
}

internal const val DOC_WEB_VIEW_BROADCAST_ACTION = "ru.tensor.sbis.docwebviewer.WEB_VIEW_DEBUG_BROADCAST"
private const val ENABLE_WEB_VIEW_DEBUG = "ENABLE_WEB_VIEW_DEBUG"
private const val APP_METRICA_PROCESS_SUFFIX = "Metrica"