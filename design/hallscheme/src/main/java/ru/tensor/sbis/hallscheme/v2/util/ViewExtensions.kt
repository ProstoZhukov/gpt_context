package ru.tensor.sbis.hallscheme.v2.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.RelativeLayout
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem

/**
 * Вычисление позиции и размеров вью для элемента схемы зала.
 * @author aa.gulevskiy
 */
internal fun View.evaluateLayoutParams(hallSchemeItem: HallSchemeItem) {
    with(hallSchemeItem) {
        val w = rect.right - rect.left
        val h = rect.bottom - rect.top

        layoutParams = RelativeLayout.LayoutParams(w, h)

        this@evaluateLayoutParams.x = rect.left.toFloat()
        this@evaluateLayoutParams.y = rect.top.toFloat()
    }
}

/**
 * Поворачивает вью в зависимости от заданного угла.
 */
internal fun View.rotateItem(hallSchemeItem: HallSchemeItem) {
    with(hallSchemeItem) {
        if (itemRotation == 0) {
            return
        }

        rotation = itemRotation.toFloat()

        when (itemRotation) {
            90, 270 -> {
                val w = rect.right - rect.left
                val h = rect.bottom - rect.top
                val sizesDiffAfterRotation = w / 2 - h / 2
                translationX = x - sizesDiffAfterRotation.toFloat()
                translationY = y + sizesDiffAfterRotation.toFloat()
            }
        }
    }
}

/**
 * Получение контекста activity.
 * В некоторых приложениях (Retail, Presto) DisplayMetrics могут быть изменены
 * на уровне контекста активити (масштабирование).
 */
internal fun Context.getActivityContext(): Context {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        ctx = ctx.baseContext
    }
    return this
}