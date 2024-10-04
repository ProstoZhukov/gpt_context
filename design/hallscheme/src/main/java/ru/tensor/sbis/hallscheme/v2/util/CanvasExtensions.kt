package ru.tensor.sbis.hallscheme.v2.util

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Build

/**
 * Вспомогательные функции для работы с [Canvas].
 */

/**
 * Задаёт область на канве, внутри которой не будут отрисовываться объекты.
 */
internal inline fun Canvas.withClipOut(
    clipPath: Path,
    block: Canvas.() -> Unit
) {
    val checkpoint = save()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        clipOutPath(clipPath)
    } else {
        clipPath(clipPath, Region.Op.DIFFERENCE)
    }
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}
