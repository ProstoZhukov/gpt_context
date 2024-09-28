package ru.tensor.sbis.widget_player.widget.text

import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.forEach
import kotlin.math.min

/**
 * @author am.boldinov
 */
internal class FormattedLayout(
    private val delegate: StaticLayout,
    private val leftIndents: IntArray?,
    private val rightIndents: IntArray?
) : Layout(
    delegate.text,
    delegate.paint,
    delegate.width,
    delegate.alignment,
    delegate.spacingMultiplier,
    delegate.spacingAdd
) {

    private val lineOffsetStore = SparseIntArray()
    private var lineWidthCache: SparseArray<Float>? = null

    fun updateLineTopOffset(line: Int, offset: Int) {
        lineOffsetStore.append(line, offset)
    }

    fun clearTopOffsets() {
        lineOffsetStore.clear()
    }

    fun getOffsetBeforeLine(line: Int): Int {
        var offset = 0
        lineOffsetStore.forEach { key, value ->
            if (key < line) {
                offset += value
            }
        }
        return offset
    }

    @Suppress("KotlinConstantConditions", "unused")
    fun getIndentAdjust(line: Int, align: Alignment): Int {
        if (alignment == Alignment.ALIGN_NORMAL || align.ordinal == 4) {
            return if (leftIndents == null) {
                0
            } else {
                leftIndents[min(line, leftIndents.size - 1)]
            }
        } else if (alignment == Alignment.ALIGN_OPPOSITE || align.ordinal == 5) {
            return if (rightIndents == null) {
                0
            } else {
                -rightIndents[min(line, rightIndents.size - 1)]
            }
        } else if (align == Alignment.ALIGN_CENTER) {
            var left = 0
            if (leftIndents != null) {
                left = leftIndents[min(line, leftIndents.size - 1)]
            }
            var right = 0
            if (rightIndents != null) {
                right = rightIndents[min(line, rightIndents.size - 1)]
            }
            return (left - right) shr 1
        } else {
            return 0
        }
    }

    override fun getLineForVertical(vertical: Int): Int {
        return delegate.getLineForVertical(vertical)
    }

    override fun getLineForOffset(offset: Int): Int {
        return delegate.getLineForOffset(offset)
    }

    override fun getLineCount(): Int {
        return delegate.lineCount
    }

    fun getLineHeight(line: Int): Int {
        return delegate.getLineTop(line + 1) - delegate.getLineTop(line)
    }

    override fun getLineTop(line: Int): Int {
        return delegate.getLineTop(line) + getOffsetBeforeLine(line)
    }

    override fun getLineDescent(line: Int): Int {
        return delegate.getLineDescent(line)
    }

    override fun getLineStart(line: Int): Int {
        return delegate.getLineStart(line)
    }

    override fun getParagraphDirection(line: Int): Int {
        return delegate.getParagraphDirection(line)
    }

    override fun getLineContainsTab(line: Int): Boolean {
        return delegate.getLineContainsTab(line)
    }

    override fun getLineDirections(line: Int): Directions {
        return delegate.getLineDirections(line)
    }

    override fun getTopPadding(): Int {
        return delegate.topPadding
    }

    override fun getBottomPadding(): Int {
        return delegate.bottomPadding
    }

    override fun getEllipsisStart(line: Int): Int {
        return delegate.getEllipsisStart(line)
    }

    override fun getEllipsisCount(line: Int): Int {
        return delegate.getEllipsisCount(line)
    }

    override fun getEllipsizedWidth(): Int {
        return delegate.ellipsizedWidth
    }

    override fun getHeight(): Int {
        return delegate.height
    }

    override fun getLineBounds(line: Int, bounds: Rect?): Int {
        return delegate.getLineBounds(line, bounds)
    }

    override fun isFallbackLineSpacingEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            delegate.isFallbackLineSpacingEnabled
        } else {
            false
        }
    }

    override fun isRtlCharAt(offset: Int): Boolean {
        return delegate.isRtlCharAt(offset)
    }

    override fun getPrimaryHorizontal(offset: Int): Float {
        return delegate.getPrimaryHorizontal(offset)
    }

    override fun getSecondaryHorizontal(offset: Int): Float {
        return delegate.getSecondaryHorizontal(offset)
    }

    override fun getLineLeft(line: Int): Float {
        return delegate.getLineLeft(line)
    }

    override fun getLineRight(line: Int): Float {
        return delegate.getLineRight(line)
    }

    override fun getLineMax(line: Int): Float {
        return delegate.getLineMax(line)
    }

    override fun getLineWidth(line: Int): Float {
        return lineWidthCache?.get(line, -1f).takeIf { it != -1f } ?: run {
            delegate.getLineWidth(line).also { width ->
                (lineWidthCache ?: SparseArray<Float>().also {
                    lineWidthCache = it
                }).put(line, width)
            }
        }
    }

    override fun getOffsetForHorizontal(line: Int, horiz: Float): Int {
        return delegate.getOffsetForHorizontal(line, horiz)
    }

    override fun getLineVisibleEnd(line: Int): Int {
        return delegate.getLineVisibleEnd(line)
    }

    override fun getOffsetToLeftOf(offset: Int): Int {
        return delegate.getOffsetToLeftOf(offset)
    }

    override fun getOffsetToRightOf(offset: Int): Int {
        return delegate.getOffsetToRightOf(offset)
    }

    override fun getCursorPath(point: Int, dest: Path?, editingBuffer: CharSequence?) {
        delegate.getCursorPath(point, dest, editingBuffer)
    }

    override fun getSelectionPath(start: Int, end: Int, dest: Path?) {
        delegate.getSelectionPath(start, end, dest)
    }
}