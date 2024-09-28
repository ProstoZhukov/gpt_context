package ru.tensor.sbis.message_panel.helper

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace

/**
 * Диспетчер метрик Firebase Performance для регистрации трейсов панели сообщений.
 *
 * @author vv.chekurda
 */
internal object MessagePanelMetricsDispatcher {

    private val activeTraceSpace = HashMap<String, Trace>()

    /**
     * Начать метрику
     *
     * @param name название метрики
     */
    fun startTrace(name: String) {
        activeTraceSpace[name] = FirebasePerformance.startTrace(name)
    }

    /**
     * Закончить метрику
     *
     * @param name название метрики
     */
    fun stopTrace(name: String) {
        activeTraceSpace.run {
            if (containsKey(name)){
                get(name)?.stop()
                remove(name)
            }
        }
    }
}