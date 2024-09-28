package ru.tensor.sbis.widget_player.layout.inline.node

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.layout.MultiLineView
import ru.tensor.sbis.widget_player.layout.inline.InlineMeasureStep
import ru.tensor.sbis.widget_player.layout.inline.InlineMeasurer
import ru.tensor.sbis.widget_player.layout.inline.InlineOffset
import ru.tensor.sbis.widget_player.layout.inline.MutableInlineOffset
import ru.tensor.sbis.widget_player.layout.inline.InlineStrategy
import ru.tensor.sbis.widget_player.layout.inline.LineBounds
import ru.tensor.sbis.widget_player.layout.inline.MutableLineBounds

private const val MINIMAL_TEXT_WIDTH_PX = 70

/**
 * @author am.boldinov
 */
internal class BufferedLineNode : InlineNode<MultiLineView> {

    private var _source: MultiLineView? = null
    private var _strategy: InlineStrategy? = null

    private val view get() = _source!!
    private val androidView get() = view as View
    private val line
        get() = when (_strategy!!) {
            InlineStrategy.BEFORE -> view.getLineCount() - 1 // last line
            InlineStrategy.AFTER -> 0 // first line
        }

    private val offset = MutableInlineOffset()
    private val lineBounds = MutableLineBounds()
    private var leftIndentChanged = false

    override fun init(source: MultiLineView, strategy: InlineStrategy) {
        _source = source
        _strategy = strategy
        leftIndentChanged = false
    }

    override fun release() {
        _source = null
        _strategy = null
    }

    override fun inline(x: Int, y: Int): InlineOffset {
        androidView.layoutParams?.apply {
            if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = ViewGroup.LayoutParams.MATCH_PARENT // рендер на всю доступную ширину
            }
        }
        return offset.apply {
            set(0, y)
            view.beforeMeasureUpdater.updateLeftLineIndent(line, x).also {
                leftIndentChanged = it
            }
        }
    }

    override fun isFitToWidth(width: Int): Boolean {
        return width >= MINIMAL_TEXT_WIDTH_PX // TODO необходимо посчитать ширину первого слова до пробела и сравнить с availableWidth (используя PrecomputedText)
    }

    override fun onMeasureBeforeInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    ) {
        // ignore
    }

    override fun onMeasureAfterInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    ) {
        when (step) {
            is InlineMeasureStep.Baseline -> {
                if (leftIndentChanged) {
                    measurer.measure(androidView, widthMeasureSpec, heightMeasureSpec)
                } else if (step.lineHeight > computeBounds().height()) {
                    view.afterMeasureUpdater.updateLineHeight(line, step.lineHeight)
                }
            }

            InlineMeasureStep.Inline -> {
                measurer.measure(androidView, widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    override fun computeBounds(): LineBounds {
        return lineBounds.apply {
            left = view.getLeftLineIndent(line)
            top = view.getLineTop(line)
            right = left + view.getLineWidth(line)
            bottom = view.getLineBottom(line)
        }
    }

    override fun getLineCount() = view.getLineCount()

}