package ru.tensor.sbis.communicator.core.views.conversation_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import androidx.core.graphics.withSave
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.chats_pinnedBackgroundPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.chats_pinnedIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.chats_pinnedIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.createChatItemsResources
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**
 * View иконки закрепленного канала для реестра каналов.
 *
 * @author vv.chekurda
 */
internal class PinnedIconView(context: Context) : View(context) {

    private val layoutSize = context.getDimenPx(RDesign.attr.inlineHeight_6xs)
    private val backgroundRadius = (layoutSize / 2).toFloat()
    private val iconLayout = TextLayout {
        createChatItemsResources(context)
        text = chats_pinnedIconText
        paint = chats_pinnedIconPaint
        paint.textSize = context.getDimen(RDesign.attr.iconSize_s)
        includeFontPad = false
    }

    private val backgroundRect = RectF()
    private val path = Path()

    init {
        chats_pinnedBackgroundPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(layoutSize, layoutSize)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setLayoutBounds(left, top, right, bottom)
    }

    private fun setLayoutBounds(left: Int, top: Int, right: Int, bottom: Int) {
        backgroundRect.set(
            0f,
            0f,
            right - left.toFloat(),
            bottom - top.toFloat()
        )
        iconLayout.layout(
            (measuredWidth - iconLayout.width) / 2,
            (measuredHeight - iconLayout.height) / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.withSave {
            drawCircle(backgroundRadius, backgroundRadius, backgroundRadius, chats_pinnedBackgroundPaint)
            iconLayout.draw(this)
        }
    }
}
