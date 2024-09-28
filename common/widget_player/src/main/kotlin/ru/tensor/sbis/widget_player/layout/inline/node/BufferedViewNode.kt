package ru.tensor.sbis.widget_player.layout.inline.node

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.MarginLayoutParams
import ru.tensor.sbis.design.utils.extentions.getFullMeasuredWidth
import ru.tensor.sbis.widget_player.layout.inline.InlineMeasureStep
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.layout.inline.InlineMeasurer
import ru.tensor.sbis.widget_player.layout.inline.InlineOffset
import ru.tensor.sbis.widget_player.layout.inline.MutableInlineOffset
import ru.tensor.sbis.widget_player.layout.inline.InlineStrategy
import ru.tensor.sbis.widget_player.layout.inline.LineBounds
import ru.tensor.sbis.widget_player.layout.inline.MutableLineBounds

/**
 * @author am.boldinov
 */
internal class BufferedViewNode : InlineNode<View> {

    private var _source: View? = null

    private val view get() = _source!!

    private val offset = MutableInlineOffset()
    private val lineBounds = MutableLineBounds()

    private val minHorizontalMargin = DimenRes.attr(DesignAttr.offset_3xs)

    override fun init(source: View, strategy: InlineStrategy) {
        _source = source
    }

    override fun release() {
        _source = null
    }

    override fun inline(x: Int, y: Int): InlineOffset {
        val lp = view.layoutParams as MarginLayoutParams
        val margin = minHorizontalMargin.getValuePx(view.context)
        lp.leftMargin = maxOf(lp.leftMargin, margin)
        lp.rightMargin = maxOf(lp.rightMargin, margin)
        return offset.apply {
            set(x + lp.leftMargin, y)
        }
    }

    override fun isFitToWidth(width: Int): Boolean {
        return width >= view.getFullMeasuredWidth()
    }

    override fun onMeasureBeforeInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    ) {
        when (step) {
            is InlineMeasureStep.Baseline -> {
                if (step.lineHeight > computeBounds().height()) {
                    val lp = view.layoutParams as MarginLayoutParams
                    val diff = step.lineHeight - (view.measuredHeight + lp.topMargin + lp.bottomMargin)
                    if (diff > 0) {
                        lp.topMargin += diff / 2
                    }
                    /* Для текущих задач достаточно изменить margin, в случае появления исключительных случаев вернуть измерение
                    measurer.measure(
                        view,
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(step.lineHeight, MeasureSpec.EXACTLY)
                    )*/
                }
            }

            InlineMeasureStep.Inline -> {
                measurer.measure(view, widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    override fun onMeasureAfterInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    ) {
        // ignore
    }

    override fun computeBounds(): LineBounds {
        val lp = view.layoutParams as MarginLayoutParams
        return lineBounds.apply {
            left = lp.leftMargin
            top = 0
            right = left + view.measuredWidth + lp.rightMargin
            bottom = lp.topMargin + view.measuredHeight + lp.bottomMargin
        }
    }

    override fun getLineCount() = 1

}