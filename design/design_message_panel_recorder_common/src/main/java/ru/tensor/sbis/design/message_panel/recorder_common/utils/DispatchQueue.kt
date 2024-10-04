package ru.tensor.sbis.design.message_panel.recorder_common.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.CallSuper
import timber.log.Timber
import java.util.concurrent.CountDownLatch

/**
 * Фоновая очередь для исполнения на базе [Handler].
 * Необходима для облегчения создания высоко-приоритетных потоков и обращения с ними.
 *
 * @param threadName название потока.
 *
 * @author vv.chekurda
 */
open class DispatchQueue(threadName: String) : Thread() {

    @Volatile
    protected var handler: Handler? = null
    @Volatile
    private var isStarted: Boolean = false
    private val syncLatch = CountDownLatch(1)

    init {
        name = threadName
    }

    override fun start() {
        if (!isStarted) {
            super.start()
            isStarted = true
        }
    }

    protected open fun handleMessage(inputMessage: Message) = Unit

    fun sendMessage(message: Message, delay: Long) {
        onHandler {
            if (delay > 0) {
                sendMessage(message)
            } else {
                sendMessageDelayed(message, delay)
            }
        }
    }

    fun post(action: () -> Unit): Boolean =
        postDelayed { action() }

    fun postDelayed(delay: Long = 0, action: () -> Unit): Boolean =
        onHandler {
            if (delay > 0) {
                postDelayed(action, delay)
            } else {
                post(action)
            }
        }

    fun cancelAction(action: () -> Unit) {
        onHandler { removeCallbacks(action) }
    }

    fun cancelActions(vararg actions: () -> Unit) {
        actions.forEach(::cancelAction)
    }

    fun cleanupQueue() {
        onHandler { removeCallbacksAndMessages(null) }
    }

    fun requireHandler(): Handler = handler!!

    override fun run() {
        Looper.prepare()
        @Suppress("DEPRECATION")
        @SuppressLint("HandlerLeak")
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                this@DispatchQueue.handleMessage(msg)
            }
        }
        syncLatch.countDown()
        Looper.loop()
    }

    @CallSuper
    open fun recycle() {
        if (isStarted) {
            onHandler { looper.quit() }
        }
    }

    override fun interrupt() {
        if (isStarted) {
            onHandler { looper.quit() }
        }
        super.interrupt()
    }

    protected fun onHandler(action: Handler.() -> Unit): Boolean =
        try {
            if (!isStarted) start()
            syncLatch.await()
            action(requireHandler())
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
}