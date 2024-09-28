@file:JvmName("ContextUtils")

package ru.tensor.sbis.common.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process

/**
 * Возвращает true если процесс приложения в данный момент связан с интерфейсом переднего плана вверху экрана,
 * т.е. с которой взаимодействует пользователь
 */
fun Context.isAppProcessForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcess = activityManager.runningAppProcesses?.firstOrNull { it.pid == Process.myPid() }
    return runningAppProcess != null && runningAppProcess.importance <= IMPORTANCE_FOREGROUND
}

/**
 * Возвращает true если приложение в состоянии ограничения на использование фоновых сервисов
 * Подробнее: https://developer.android.com/about/versions/oreo/background.html
 */
fun Context.isAppBackgroundServiceLimited(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isAppProcessForeground()
}

/**
 * Получить имя процесса, в котором запущен [application]
 */
fun getProcessName(application: Application): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName() ?: getProcessNameFromPid(application)
    } else {
        getProcessNameFromPid(application)
    }
}

private fun getProcessNameFromPid(application: Application): String? {
    val pid = Process.myPid()
    return findRunningProcess(application) { it.pid == pid }?.processName
}

private fun findRunningProcess(
    applicationContext: Context,
    matcher: (ActivityManager.RunningAppProcessInfo) -> Boolean
): ActivityManager.RunningAppProcessInfo? {
    val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return manager?.runningAppProcesses?.filterNotNull()?.firstOrNull(matcher)
}

/**
 * Есть ли хотя бы один запущенный сервис в приложении.
 *
 * Можно использовать при старте Application для определения целей запуска.
 * Считаем, что если нет сервисов, то запуск производится для дальнейшего запуска активности.
 */
@Suppress("deprecation")
fun Context.hasLaunchedServices(): Boolean = try {
    val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    manager?.getRunningServices(ONLY_ONE_SERVICE)?.isNotEmpty() ?: false
} catch (ex: SecurityException) {
    false
}

private const val ONLY_ONE_SERVICE = 1