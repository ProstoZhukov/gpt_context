package ru.tensor.sbis.plugin_manager

import androidx.tracing.Trace

/**
 * Трейсер
 *
 * @author kv.martyshenko
 */
interface Tracer {

    /**
     * Включин ли трейсинг
     */
    fun isTracingEnabled(): Boolean

    /**
     * Метод для трейсинга участков
     *
     * @param name название блока
     * @param block
     */
    fun trace(name: String, block: () -> Unit)

    companion object
}

/**
 * Трейсер на основе нативных инструментах android
 *
 * @author kv.martyshenko
 */
internal class AndroidTracer : Tracer {

    override fun isTracingEnabled(): Boolean {
        return Trace.isEnabled()
    }

    override fun trace(name: String, block: () -> Unit) {
        return androidx.tracing.trace(name, block)
    }

}

/**
 * Метод для получения дефолтного трейсера на основе нативных средств Android
 */
fun Tracer.Companion.androidTracer(): Tracer = AndroidTracer()

/**
 * Трейсер-заглушка
 *
 * @author kv.martyshenko
 */
internal object FakeTracer : Tracer {

    override fun isTracingEnabled(): Boolean {
        return true
    }

    override fun trace(name: String, block: () -> Unit) {
        block()
    }

}

/**
 * Метод для получения трейсера-заглушки
 */
fun Tracer.Companion.fake(): Tracer = FakeTracer