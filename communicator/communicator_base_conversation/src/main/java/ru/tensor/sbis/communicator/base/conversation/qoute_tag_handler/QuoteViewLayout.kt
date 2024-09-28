package ru.tensor.sbis.communicator.base.conversation.qoute_tag_handler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spannable
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.richtext.util.SpannableUtil
import ru.tensor.sbis.richtext.view.CloneableRichViewFactory
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.richtext.R as RRich

/**
 * Layout для отрисовки цитаты, имеющий линию перед текстом цитаты.
 *
 * @author da.zhukov
 */
@SuppressLint("ViewConstructor")
internal class QuoteViewLayout(
    context: Context,
    richViewFactory: CloneableRichViewFactory
) : FrameLayout(context) {

    private val richViewLayout = richViewFactory.cloneView().apply {
        textView.maxLines = 3 // устанавливаем значение равное 3 т.к. необходимо учесть ФИО
    }

    private val linePaint = Paint().apply {
        strokeWidth = context.resources.getDimensionPixelSize(RRich.dimen.richtext_block_quote_line_width).toFloat()
        color = ContextCompat.getColor(context, RDesign.color.text_color_accent_1)
        style = Paint.Style.STROKE
    }

    /** размер отступа до линии. */
    private var textBlockStartPadding = 0

    init {
        setWillNotDraw(false)
        textBlockStartPadding = context.getDimenPx(RDesign.attr.offset_xs)
        richViewLayout.setPadding(textBlockStartPadding, 0, 0, 0)
        addView(richViewLayout)
    }

    fun setContent(content: Spannable) {
        this.richViewLayout.setText(content)
    }

    fun recycle() {
        richViewLayout.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x: Int = SpannableUtil.LEADING_MARGIN_OFFSET_X
        canvas.drawLine(
            x.toFloat(),
            richViewLayout.top.toFloat(),
            x.toFloat(),
            richViewLayout.bottom.toFloat(),
            linePaint
        )
    }
}