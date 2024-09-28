package ru.tensor.sbis.widget_player.layout.inline

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.isGone
import ru.tensor.sbis.widget_player.layout.MultiLineView
import ru.tensor.sbis.widget_player.layout.inline.node.BufferedLineNode
import ru.tensor.sbis.widget_player.layout.inline.node.BufferedViewNode
import ru.tensor.sbis.widget_player.layout.inline.node.InlineNode
import ru.tensor.sbis.widget_player.layout.inline.node.InlineNodePool

/**
 * @author am.boldinov
 */
@RequiresApi(Build.VERSION_CODES.M)
class InlineLayout(context: Context) : ViewGroup(context), InlineMeasurer {

    private companion object {

        private val multiLineNodePool = InlineNodePool(5) {
            BufferedLineNode()
        }
        private val viewNodePool = InlineNodePool(5) {
            BufferedViewNode()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentWidthMeasureSpec = if (childCount > 1) { // если несколько чайлдов - инлайним на всю доступную ширину
            MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)
        } else {
            widthMeasureSpec
        }

        val info = MeasureInfo()
        measureChildren(
            widthMeasureSpec = parentWidthMeasureSpec,
            heightMeasureSpec = heightMeasureSpec,
            info = info,
            step = MeasureStep.INLINE
        )

        if (childCount > 1 && info.lineHeightChanged) {
            // если хотя бы на одной строке высоты виджетов отличаются,
            // то необходимо корректировать высоты строк всего layout
            measureChildren(
                widthMeasureSpec = parentWidthMeasureSpec,
                heightMeasureSpec = heightMeasureSpec,
                info = info,
                step = MeasureStep.BASELINE
            )
        }

        val width = info.width
        val height = maxOf(info.height + paddingTop + paddingBottom, suggestedMinimumHeight)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingStart
        val top = paddingTop
        children.forEach {
            if (!it.isGone) {
                val lp = it.layoutParams as LayoutParams
                val width = it.measuredWidth
                val height = it.measuredHeight
                val childLeft = left + lp.x
                val childTop = top + lp.y + lp.topMargin
                it.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    override fun measure(view: View, widthMeasureSpec: Int, heightMeasureSpec: Int, withMargins: Boolean) {
        val lp = view.layoutParams as LayoutParams

        var widthPadding = paddingLeft + paddingRight
        if (withMargins) {
            widthPadding += lp.leftMargin + lp.rightMargin
        }
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, widthPadding, lp.width)

        var heightPadding = paddingTop + paddingBottom
        if (withMargins) {
            heightPadding += lp.topMargin + lp.bottomMargin
        }
        val heightDimension = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            lp.height
        }
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, heightDimension)

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun getBaseline(): Int {
        if (childCount > 0) {
            return getChildAt(0).baseline
        }
        return super.getBaseline()
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        if (lp is MarginLayoutParams) {
            return LayoutParams(lp)
        }
        return LayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    class LayoutParams : MarginLayoutParams {

        internal var lineBefore = 0
        internal var lineAfter = 0
        internal var x = 0
        internal var y = 0

        internal fun offset(offset: InlineOffset) {
            x = offset.x
            y = offset.y
        }

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: ViewGroup.LayoutParams) : super(source)
    }

    private fun measureChildren(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        info: MeasureInfo,
        step: MeasureStep
    ) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        var prevView: View? = null
        var lineCursor = 0
        for (index in 0 until childCount) {
            val targetView = getChildAt(index)
            if (targetView.isGone) {
                continue
            }
            val targetParams = targetView.layoutParams as LayoutParams
            prevView?.let { baseView ->
                val baseParams = baseView.layoutParams as LayoutParams
                // расставляем дочерние View последовательно слева направо
                inlinePair(baseView, targetView, InlineDirection.RIGHT) { baseNode, targetNode ->
                    val beforeMeasureStep = when (step) {
                        MeasureStep.INLINE -> InlineMeasureStep.Inline
                        MeasureStep.BASELINE -> InlineMeasureStep.Baseline(info.getLineHeight(lineCursor))
                    }
                    targetNode.onMeasureBeforeInline(
                        widthMeasureSpec,
                        heightMeasureSpec,
                        this,
                        beforeMeasureStep
                    )
                    val baseBounds = baseNode.computeBounds()
                    val freeWidth = availableWidth - baseParams.x - baseBounds.right
                    var lineCursorOffset = 0
                    val inlineOffset = if (freeWidth > 0 && targetNode.isFitToWidth(freeWidth)) {
                        // размещаем справа от baseNode
                        targetNode.inline(
                            x = baseParams.x + baseBounds.right,
                            y = baseParams.y + baseBounds.top
                        )
                    } else {
                        // targetNode не помещается справа - перемещаем на новую строку под baseView
                        lineCursorOffset++
                        targetNode.inline(
                            x = 0,
                            y = baseParams.y + baseView.measuredHeight + baseParams.topMargin + baseParams.bottomMargin
                        )
                    }
                    val afterMeasureStep = when (step) {
                        MeasureStep.INLINE -> InlineMeasureStep.Inline
                        MeasureStep.BASELINE -> {
                            val lineHeight = if (lineCursorOffset > 0) {
                                // переключение на новую строку - необходимо вычислить больше ли текущая нода этой строки
                                val targetHeight = targetNode.computeBounds().height()
                                val lineNumber = lineCursor + lineCursorOffset
                                val offsetLineHeight = info.getLineHeight(lineNumber)
                                if (targetHeight > offsetLineHeight) {
                                    info.updateLineHeight(lineNumber, targetHeight)
                                    targetHeight
                                } else {
                                    offsetLineHeight
                                }
                            } else {
                                info.getLineHeight(lineCursor)
                            }
                            InlineMeasureStep.Baseline(lineHeight)
                        }
                    }
                    targetNode.onMeasureAfterInline(widthMeasureSpec, heightMeasureSpec, this, afterMeasureStep)
                    if (index == 1) { // оптимизация без создания node для единственной View
                        lineCursor += baseNode.getLineCount() - 1
                        baseParams.lineBefore = 0
                        baseParams.lineAfter = lineCursor
                    }
                    lineCursor += lineCursorOffset
                    targetParams.lineBefore = lineCursor
                    lineCursor += targetNode.getLineCount() - 1
                    targetParams.lineAfter = lineCursor

                    targetParams.offset(inlineOffset)

                    info.height = maxOf(
                        baseParams.y + baseView.measuredHeight + baseParams.topMargin + baseParams.bottomMargin,
                        targetParams.y + targetView.measuredHeight + targetParams.topMargin + targetParams.bottomMargin
                    )
                    info.width = availableWidth // несколько инлайн виджетов - рендерим на всю ширину

                    if (step == MeasureStep.INLINE) {
                        info.updateLineHeight(baseParams.lineAfter, baseBounds.height())
                        val targetHeight = targetNode.computeBounds().height()
                        info.updateLineHeight(targetParams.lineBefore, targetHeight)
                    }
                }
            } ?: run {
                when (step) {
                    MeasureStep.INLINE -> {
                        measure(targetView, widthMeasureSpec, heightMeasureSpec, withMargins = true)
                    }

                    MeasureStep.BASELINE -> {
                        targetView.inline(InlineStrategy.BEFORE) { node ->
                            val measureStep = InlineMeasureStep.Baseline(info.getLineHeight(lineCursor))
                            node.onMeasureBeforeInline(widthMeasureSpec, heightMeasureSpec, this, measureStep)
                            node.onMeasureAfterInline(widthMeasureSpec, heightMeasureSpec, this, measureStep)
                        }
                    }
                }

                info.height = targetView.measuredHeight + targetParams.topMargin + targetParams.bottomMargin
                info.width = targetView.measuredWidth + targetParams.leftMargin + targetParams.rightMargin +
                    paddingStart + paddingEnd
                targetParams.x = 0
                targetParams.y = 0
            }
            prevView = targetView
        }
    }

    @Suppress("SameParameterValue")
    private inline fun inlinePair(
        baseView: View,
        targetView: View,
        direction: InlineDirection,
        action: (baseNode: InlineNode<*>, targetNode: InlineNode<*>) -> Unit
    ) {
        val baseStrategy: InlineStrategy
        val targetStrategy: InlineStrategy
        if (direction == InlineDirection.RIGHT) {
            baseStrategy = InlineStrategy.BEFORE
            targetStrategy = InlineStrategy.AFTER
        } else {
            baseStrategy = InlineStrategy.AFTER
            targetStrategy = InlineStrategy.BEFORE
        }
        baseView.inline(strategy = baseStrategy) { baseNode ->
            targetView.inline(strategy = targetStrategy) { targetNode ->
                action.invoke(baseNode, targetNode)
            }
        }
    }

    private inline fun View.inline(strategy: InlineStrategy, action: (InlineNode<*>) -> Unit) {
        if (this is MultiLineView) {
            multiLineNodePool.takeNodeWithAction(this, strategy, action)
        } else {
            viewNodePool.takeNodeWithAction(this, strategy, action)
        }
    }

    private inline fun <reified SOURCE> InlineNodePool<SOURCE>.takeNodeWithAction(
        source: SOURCE, strategy: InlineStrategy, action: (InlineNode<*>) -> Unit
    ) {
        take().let {
            it.init(source, strategy)
            action.invoke(it)
            it.release()
            put(it)
        }
    }

    private enum class InlineDirection {
        RIGHT,
        LEFT
    }

    private enum class MeasureStep {
        INLINE, // расстановка элементов рядом друг с другом
        BASELINE // выравнивание элементов по высоте
    }

    private class MeasureInfo {
        var width: Int = 0
        var height: Int = 0

        var lineHeightChanged = false
            private set

        private val lineHeight = SparseIntArray(5)

        private val lineHeightReservePx = 2

        fun getLineHeight(line: Int): Int {
            return lineHeight.get(line, 0)
        }

        fun updateLineHeight(line: Int, height: Int) {
            val current = getLineHeight(line)
            if (height > current) {
                if (current > 0) {
                    // не обновляем высоту в случае если есть незначительная погрешность для исключения повторных вычислений
                    if (height - current > lineHeightReservePx) {
                        lineHeightChanged = true
                        lineHeight.put(line, height)
                    }
                } else {
                    lineHeight.put(line, height)
                }
            }
        }
    }
}