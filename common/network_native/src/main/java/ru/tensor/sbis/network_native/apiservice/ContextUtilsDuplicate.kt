@file:JvmName("ContextUtilsDuplicate")

package ru.tensor.sbis.network_native.apiservice

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.content.Context
import android.os.Build
import android.os.Process

/**
 * Возвращает true если процесс приложения в данный момент связан с интерфейсом переднего плана вверху экрана,
 * т.е. с которой взаимодействует пользователь
 */
@Deprecated(
    "Для модуля Network выполнено намеренное дублирование, как временная мера " +
            "для развязки модулей core и common, оригинальные утилиты перенесены в модуль common",
    ReplaceWith("ru.tensor.sbis.common.util.ContextUtils")
)
fun Context.isAppProcessForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcess = activityManager.runningAppProcesses?.first { it.pid == Process.myPid() }
    return runningAppProcess != null && runningAppProcess.importance <= IMPORTANCE_FOREGROUND
}

/**
 * Возвращает true если приложение в состоянии ограничения на использование фоновых сервисов
 * Подробнее: https://developer.android.com/about/versions/oreo/background.html
 */
@Deprecated(
    "Для модуля Network выполнено намеренное дублирование, как временная мера " +
            "для развязки модулей core и common, оригинальные утилиты перенесены в модуль common",
    ReplaceWith("ru.tensor.sbis.common.util.ContextUtils")
)
fun Context.isAppBackgroundServiceLimited(): Boolean {
    @Suppress("DEPRECATION")
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isAppProcessForeground()
}