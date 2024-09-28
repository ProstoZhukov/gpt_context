package ru.tensor.sbis.controller_utils.loading

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.platform.StartupObseverStorage
import ru.tensor.sbis.platform.generated.StartupObserver

/**
 * Утилита для получения статусов инициализации платформы.
 *
 * @author ar.leschev
 */
object StartupExposer {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _msgFlow: MutableSharedFlow<String> = MutableSharedFlow()

    private val callback by lazy {
        object : StartupObserver() {
            private val subscribeTimeWithDelay = System.currentTimeMillis() + DELAY_MS
            private var time = System.currentTimeMillis()
            private var prevMes = ""

            override fun message(mes: String) {
                val action: () -> Unit = { scope.launch { _msgFlow.emit(mes) } }
                if (BuildConfig.DEBUG) {
                    //Для удобного определения модулей контроллера с долгой загрузкой, [timeC] на усмотрение
                    val timeC = System.currentTimeMillis() - time
                    if (timeC > 25) Log.e("StartupExposer.message", "$prevMes $timeC")
                    prevMes = mes
                    time = System.currentTimeMillis()
                    if (System.currentTimeMillis() >= subscribeTimeWithDelay) action()
                } else action()
            }
        }
    }

    /** Статус инициализации платформы. */
    val event: SharedFlow<String> = _msgFlow.asSharedFlow()

    /** Установить слушатель статусов инициализации платформы (перед самой инициализацией). */
    fun start() {
        StartupObseverStorage.setObserver(callback)
    }

    /** Отменить подписки. */
    fun stop() {
        scope.cancel()
    }

    /** Отсрочка постинга в ms. */
    private const val DELAY_MS = 500
}