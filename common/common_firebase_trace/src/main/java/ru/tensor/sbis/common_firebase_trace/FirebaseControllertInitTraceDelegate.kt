package ru.tensor.sbis.common_firebase_trace

import com.google.firebase.perf.FirebasePerformance
import ru.tensor.sbis.common.tracing.FirebaseTracerDelegate

/**
 * Реализация делегата c трекингом передаваемой функции
 *
 * @author da.zolotarev
 */

fun firebaseTracer(): FirebaseTracerDelegate = {
    val controllerTrace = FirebasePerformance.startTrace("init_app_core")
    it.invoke()
    controllerTrace.stop()
}