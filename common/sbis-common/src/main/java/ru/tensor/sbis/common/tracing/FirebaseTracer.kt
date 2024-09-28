package ru.tensor.sbis.common.tracing

/**
 * Делегат делающий добавочное действие для передавемой функции
 *
 * @author da.zolotarev
 */
typealias FirebaseTracerDelegate = (block: () -> Unit) -> Unit