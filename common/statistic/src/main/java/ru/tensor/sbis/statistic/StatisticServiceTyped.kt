package ru.tensor.sbis.statistic

import android.app.Application
import androidx.annotation.AnyThread
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.statistic.model.StatisticTrace
import ru.tensor.sbis.statistic.model.StatisticTraceTyped
import ru.tensor.sbis.statistic.model.UserInfo
import timber.log.Timber
import java.util.Date

/**
 * Сервис отправки статистики.
 *
 * @author kv.martyshenko
 */
open class StatisticServiceTyped<EVENT> internal constructor() {
    private val scope = StatisticScope()

    private var storageAttached: Boolean = false
    private var userInfo: UserInfo? = null

    private val coroutineScope = CoroutineScope(
        SupervisorJob() +
            Dispatchers.Default +
            CoroutineExceptionHandler { _, error ->
                Timber.w(error, "Statistic error")
            }
    )
    private val analyticsChannel = coroutineScope.async(start = CoroutineStart.LAZY) {
        Channel<EventData<EVENT>>(
            capacity = 1_000,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    /**
     * Метод для установки [StatisticStorage].
     * Установить хранилище можно только ОДИН раз.
     */
    @AnyThread
    @Synchronized
    fun Application.setupStorage(storage: StatisticStorage<EVENT>) {
        if (storageAttached) {
            throw IllegalStateException("Storage can be installed only once!")
        }
        storageAttached = true

        startEventProcessing(storage)
    }

    /**
     * Метод для установки информации о пользователе.
     */
    @AnyThread
    @Synchronized
    fun Application.setUser(user: UserInfo?) {
        userInfo = user
    }

    /**
     * Отправить событие.
     * Метод не является блокирующим и выполняет всю работу в бэкграунде.
     *
     * @param event событие.
     */
    @AnyThread
    @Synchronized
    fun report(event: EVENT) {
        save(event, userInfo, currentTime())
    }

    /**
     * Запустить трассировку события.
     * Время начала события фиксируется в данный момент.
     * Метод не является блокирующим и выполняет всю работу в бэкграунде.
     *
     * @param event событие.
     *
     * @return [StatisticTrace] объект, на котором после завершения интересующего события,
     * нужно вызвать [StatisticTrace.stop].
     *
     */
    @AnyThread
    @Synchronized
    fun startTrace(event: EVENT): StatisticTraceTyped<EVENT> {
        val startedAt = currentTime()
        val currentUser = userInfo
        return StatisticTraceTyped(event) { statEvent ->
            save(statEvent, currentUser, startedAt, currentTime().time - startedAt.time)
        }
    }

    private fun save(event: EVENT, userInfo: UserInfo?, createdAt: Date, durationInMillis: Long? = null) {
        val eventData = EventData(event, userInfo, createdAt, durationInMillis)
        coroutineScope.launch {
            analyticsChannel.await().send(eventData)
        }
    }

    private fun currentTime() = Date()

    private fun startEventProcessing(storage: StatisticStorage<EVENT>) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val channel = analyticsChannel.await()
                for (eventData in channel) {
                    with(storage) {
                        scope.save(eventData.event, eventData.userInfo, eventData.createdAt, eventData.durationInMillis)
                    }
                }
            }
        }
    }

    private class EventData<EVENT>(
        val event: EVENT,
        val userInfo: UserInfo?,
        val createdAt: Date,
        val durationInMillis: Long?
    )

}
