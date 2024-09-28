package ru.tensor.sbis.widget_player.widget.header

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.layout.inline.InlineLayoutCompatFactory

/**
 * @author am.boldinov
 */
internal class HeaderView(context: Context) : FrameLayout(context) {

    private val layout = InlineLayoutCompatFactory.create(context)

    val childrenContainer: ViewGroup = layout

    private var bottomLinePaint: Paint? = null
        set(value) {
            if (field !== value) {
                field = value
                invalidate()
            }
        }

    init {
        setWillNotDraw(false)
        addView(layout)
    }

    /**
     * Устанавливает параметры для отрисовки линии под заголовком.
     *
     * @param padding отступ от контента заголовка до линии
     * @param paint кисть для отрисовки линии. В случае изменения параметров отрисовки
     * необходимо передать новый объект кисти.
     */
    fun setBottomLine(padding: Int, paint: Paint?) {
        updatePadding(bottom = padding)
        bottomLinePaint = paint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bottomLinePaint?.let { paint ->
            val y = height.toFloat() - paint.strokeWidth
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
    }
}