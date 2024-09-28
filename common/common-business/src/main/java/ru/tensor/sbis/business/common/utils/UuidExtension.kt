package ru.tensor.sbis.business.common.utils

import ru.tensor.sbis.common.util.UUIDUtils
import java.util.*

fun UUID.isNil(): Boolean {
    return this == UUIDUtils.NIL_UUID
}

fun UUID.isNotNil(): Boolean {
    return this != UUIDUtils.NIL_UUID
}

/**
 * Получить только валидный [UUID]
 *
 * @return валидный [UUID] или null
 */
fun UUID.valueOrNull(): UUID? =
    if (isNil()) {
        null
    } else {
        this
    }
