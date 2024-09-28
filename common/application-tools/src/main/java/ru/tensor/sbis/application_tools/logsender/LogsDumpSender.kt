/**
 * Инструмент для отправки на облако дампов логов, формируемых при сбое приложения
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.application_tools.logsender

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import androidx.annotation.WorkerThread
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import ru.tensor.sbis.network_native.httpclient.Server
import timber.log.Timber
import java.util.concurrent.Semaphore

/**
 * Проверяет наличие файлов с логами, отправляет их на облако и, в случае успешной отправки, удаляет их
 */
internal fun checkAndSendLogsDump(context: Context, getProcessName: (Context) -> String?) {
    if (isLogsDumpSenderProcess(context, getProcessName)) {
        sendLogsInAuxiliaryProcess(context)
    } else {
        sendLogsInMainProcess(context)
    }
}

/**
 * При наличии файлов с логами инициирует запуск нового процесса приложения, в котором при вызове метода
 * [checkAndSendLogsDump] будет выполнена отправка логов, после чего процесс будет завершён.
 * Запуск такого процесса требуется, если приложение упало, чтобы гарантировать доставку логов, когда падение происходит
 * во время инициализации, и основной процесс приложения может быть завершён до окончания отправки
 */
internal fun launchLogsDumpSendingProcessIfNeeded(context: Context) {
    val files = getLogFiles(context)
    if (files.isNotEmpty()) {
        context.startService(getAuxiliaryProcessServiceIntent(context))
    }
}

/** @SelfDocumented */
internal fun getProcessName(context: Context): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val pid = Process.myPid()
        manager?.runningAppProcesses?.filterNotNull()?.firstOrNull { it.pid == pid }?.processName
    }
}

/**
 * Запускает асинхронную проверку наличия файлов и отправку имеющихся логов на облако при старте приложения
 */
@SuppressLint("CheckResult")
private fun sendLogsInMainProcess(context: Context) {
    Observable.fromCallable { sendLogs(context) }
        .subscribeOn(Schedulers.io())
        .subscribe({ }, ::onSendLogsError)
}

/**
 * Выполняет отправку логов во вспомогательном процессе. Блокирует его главный поток до окончания отправки, а затем
 * завершает процесс, чтобы предотвратить выполнение дальнейшего кода, который может быть потенциально опасен
 */
@SuppressLint("CheckResult")
private fun sendLogsInAuxiliaryProcess(context: Context) {
    val semaphore = Semaphore(0)
    Observable
        .fromCallable { sendLogs(context) }
        .doFinally { semaphore.release() }
        .subscribeOn(Schedulers.io())
        .subscribe({ }, ::onSendLogsError)
    semaphore.acquire()
    context.stopService(getAuxiliaryProcessServiceIntent(context))
    Process.killProcess(Process.myPid())
}

@WorkerThread
private fun sendLogs(context: Context) {
    val files = getLogFiles(context)
    val client = OkHttpClient.Builder().build()
    getLogsData(context, files).forEach {
        val request = Request.Builder()
            .url(getUploadUrl(context))
            .addHeader("User-Agent", it.header.userAgent)
            .addHeader("X-AppPackageId", it.packageId)
            .addHeader("X-AppProjectId", it.projectId)
            .addHeader("X-AppSessionId", it.header.sessionId)
            .addHeader("X-CreationTime", it.creationDate)
            .addHeader("X-DeviceName", it.header.deviceName ?: getDeviceName())
            .addHeader("X-SBISDeviceId", it.header.deviceId ?: getDeviceId(context))
            .post(RequestBody.create("x-sbis/crashlog".toMediaTypeOrNull(), it.zippedLogFile))
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            it.logFile.delete()
            it.zippedLogFile.delete()
        } else {
            Timber.e("Logs dump sending failed. Response code: ${response.code}")
        }
        response.close()
    }
}

private fun getUploadUrl(context: Context): String {
    @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
    val host = Server.getInstance()?.host
        ?: Server.getSavedServerHost(context)
        ?: Server.Host.PROD
    return "${host.fullHostUrl}/logreceiver/service/upload/"
}

private fun getAuxiliaryProcessServiceIntent(context: Context) =
    Intent(context, LogsDumpSenderProcessService::class.java)

private fun onSendLogsError(throwable: Throwable) = Timber.e("Cannot send logs dump: ${throwable.message}")