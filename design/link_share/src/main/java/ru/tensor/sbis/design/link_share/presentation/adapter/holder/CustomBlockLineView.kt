package ru.tensor.sbis.design.link_share.presentation.adapter.holder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.utils.getThemeColorInt

/**@SelfDocumented*/
internal class CustomBlockLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintLine = Paint().apply {
        color = context.getThemeColorInt(R.attr.placeholderTextColorList)
        strokeWidth = 1f
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, 40)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val startX = 25f
        val endX = width.toFloat() - 25f

        canvas.drawLine(startX, centerY, endX, centerY, paintLine)
    }
}