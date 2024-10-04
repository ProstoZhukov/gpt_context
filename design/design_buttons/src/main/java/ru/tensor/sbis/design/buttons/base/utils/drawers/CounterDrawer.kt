package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.graphics.Canvas
import android.view.View
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterDrawable

/**
 * @author ma.kolpakov
 */
internal class CounterDrawer(
    private val button: View
) : ButtonComponentDrawer {

    private val drawable by lazy {
        SbisCounterDrawable(button.context).apply {
            callback = button
        }
    }

    var counter: SbisButtonCounter? = null
        set(value) {
            field = value
            val newCount = if (value == null) {
                0
            } else {
                drawable.style = value.style
                value.counter
            }
            if (drawable.setCount(newCount)) {
                button.requestLayout()
            }
        }

    /**
     * Отметка о том, активен ли счетчик.
     */
    var isEnabled: Boolean
        get() = drawable.isEnabled
        set(value) {
            drawable.isEnabled = value
        }

    /**
     * Счётчик пуст, если не задано ненулевое значение.
     */
    val isEmpty: Boolean
        get() = counter == null || counter?.counter == 0

    override var isVisible: Boolean = true

    override val width: Float
        get() = drawable.intrinsicWidth.toFloat()

    override val height: Float
        get() = drawable.intrinsicHeight.toFloat()

    override fun setTint(color: Int): Boolean =
        error("Unexpected method call")

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            drawable.draw(canvas)
        }
    }
}