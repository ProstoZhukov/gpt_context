package ru.tensor.sbis.communicator.common.background_sync

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.collectLatest
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.communicator.generated.MessageController

/**
 * Класс управляюший фоновой синхронизацией неотправленных сообщений. Следит за выходом из приложения.
 * Если при выходе из приложения есть неотправленные сообщения, будет запущена задача фоновой синхронизации через WorkManager.
 *
 * Синхронизация будет запущена только один раз при наличии интернет соединения, и при условии что приложение не активно,
 * уровень батареи не низкий, и устройство находится в idle режиме (не используется). См. [MessagesBackgroundSyncWorker]
 */
class MessagesBackgroundSyncManager(
    context: Context,
    private val appLifecycleTracker: AppLifecycleTracker,
    private val messageControllerProvider: DependencyProvider<MessageController>
) {
    private val appContext: Context = context.applicationContext

    init {
        // TODO https://online.sbis.ru/opendoc.html?guid=20d3d0f6-f152-41bb-942e-b10d2910fddf
        // subscribeOnAppForegroundEvents()
    }

    @SuppressLint("CheckResult")
    private suspend fun subscribeOnAppForegroundEvents() {
        appLifecycleTracker.appForegroundStateFlow
            .collectLatest(::onAppForegroundEvent)
    }

    private fun onAppForegroundEvent(foreground: Boolean) {
        if (foreground) {
            // Приложение стало активным, отменяем запланированную фоновую синхронизацию сообщений
            cancelBackgroundMessagesSync()
        } else {
            // Планируем задачу фоновой синхронизации если есть неотправленные сообщения
            if (messageControllerProvider.get().hasOutgoingMessages()) {
                MessagesBackgroundSyncWorker.schedule(appContext)
            } else {
                // Нечего синхронизировать - отменяем запланированную синхронизацию сообщений
                cancelBackgroundMessagesSync()
            }
        }
    }

    /**
     * Отменяем фоновую синхронизацию в любой непонятной ситуации - нет сообщений для отправки, приложение активно.
     */
    private fun cancelBackgroundMessagesSync() {
        MessagesBackgroundSyncWorker.unschedule(appContext)
    }
}