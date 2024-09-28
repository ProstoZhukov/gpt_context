package ru.tensor.sbis.communicator.common.background_sync

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import androidx.work.*
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.entrypoint_guard.work.EntryPointCoroutineWorker
import ru.tensor.sbis.platform.sync.generated.BackgroundSyncLauncher
import timber.log.Timber

/**
 * Класс для запуска фоновой синхронизации неотправленных сообщений.
 *
 * Синхронизация будет запущена только один раз при наличии интернет соединения, и при условии что приложение не активно,
 * уровень батареи не низкий, и устройство находится в idle режиме (не используется).
 *
 * @author rv.krohalev
 */
internal class MessagesBackgroundSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : EntryPointCoroutineWorker(context, workerParams) {

    /** @SelfDocumented */
    override suspend fun onReady(): Result {
        // Маловероятная, но возможная ситуация - приложение уже запущено. В таком случае ничего не синхронизируем
        if (applicationContext.isAppProcessForeground()) {
            return Result.success()
        }
        return try {
            // Запускаем синхронизацию только при наличии неотправленных сообщений
            if (MessageController.instance().hasOutgoingMessages()) {
                // Платформа сама управляет потоками необходимыми для синхронизации, наша задача только вызвать этот метод
                BackgroundSyncLauncher.instance().sync()
            }
            Result.success()
        } catch (error: Throwable) {
            Timber.e("BackgroundSyncLauncher error: $error")
            Result.failure()
        }
    }

    companion object {
        private val SYNC_WORK_NAME = MessagesBackgroundSyncWorker::class.java.name

        /** @SelfDocumented */
        @Synchronized
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .let {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        it.setRequiresDeviceIdle(true)
                    } else {
                        it
                    }
                }
                .build()
            val work = OneTimeWorkRequest.Builder(MessagesBackgroundSyncWorker::class.java)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(SYNC_WORK_NAME, ExistingWorkPolicy.KEEP, work)
        }

        /** @SelfDocumented */
        @Synchronized
        fun unschedule(context: Context) {
            WorkManager.getInstance(context.applicationContext)
                .cancelUniqueWork(SYNC_WORK_NAME)
        }
    }
}

/**
 * Возвращает true если процесс приложения в данный момент связан с интерфейсом переднего плана,
 * т.е. текущее приложение сейчас активно
 */
private fun Context.isAppProcessForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcess = activityManager.runningAppProcesses?.first { it.pid == Process.myPid() }
    return runningAppProcess != null && runningAppProcess.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}
