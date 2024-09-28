package ru.tensor.sbis.base_app_components.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process

/**
 * Получение имени текущего процесса
 *
 * @param application
 *
 * @author kv.martyshenko
 */
internal fun Application.getProcessName(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName() ?: getProcessNameFromPid(this, Process.myPid())
    } else {
        getProcessNameFromPid(this, Process.myPid())
    }
}

/**
 * Получение имени текущего процесса
 *
 * @param application
 * @param pid идентификатор процесса
 *
 * @author kv.martyshenko
 */
private fun getProcessNameFromPid(application: Application, pid: Int): String? {
    return findRunningProcess(application) { it.pid == pid }?.processName
}

/**
 * Метод для поиска нужного процесса в системе
 *
 * @param applicationContext
 * @param matcher
 *
 * @author kv.martyshenko
 */
private fun findRunningProcess(
    applicationContext: Context,
    matcher: (ActivityManager.RunningAppProcessInfo) -> Boolean
): ActivityManager.RunningAppProcessInfo? {
    val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return manager?.runningAppProcesses?.filterNotNull()?.firstOrNull(matcher)
}