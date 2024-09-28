package ru.tensor.sbis.common.util.shakedetection

/**
 * Промежуточный объект [ShakeDetector].
 *
 * @param timestamp метка события сенсора.
 * @param x ускорение по оси X.
 *
 * @author ar.leschev
 */
internal data class XEvent(
    val timestamp: Long,
    val x: Float
)