package ru.tensor.sbis.application_tools.logsender

import android.content.Intent
import android.os.IBinder
import ru.tensor.sbis.entrypoint_guard.service.EntryPointService

/**
 * Сервис, не выполняющий никакой работы, который требуется только для возможности запуска вспомогательного процесса
 * приложения.
 *
 * @author us.bessonov
 */
internal class LogsDumpSenderProcessService : EntryPointService() {

    override fun onReady() = Unit

    override fun onBind(intent: Intent?): IBinder? = null
}