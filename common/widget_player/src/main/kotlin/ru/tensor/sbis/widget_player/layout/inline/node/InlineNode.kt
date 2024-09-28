package ru.tensor.sbis.widget_player.layout.inline.node

import ru.tensor.sbis.widget_player.layout.inline.InlineMeasureStep
import ru.tensor.sbis.widget_player.layout.inline.LineBounds
import ru.tensor.sbis.widget_player.layout.inline.InlineMeasurer
import ru.tensor.sbis.widget_player.layout.inline.InlineOffset
import ru.tensor.sbis.widget_player.layout.inline.InlineStrategy

/**
 * @author am.boldinov
 */
internal interface InlineNode<SOURCE> {

    fun init(source: SOURCE, strategy: InlineStrategy)

    fun release()

    fun inline(x: Int, y: Int): InlineOffset

    /**
     * Может ли уместиться ли компонент в заданную ширину [width].
     */
    fun isFitToWidth(width: Int): Boolean

    fun onMeasureBeforeInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    )

    fun onMeasureAfterInline(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        measurer: InlineMeasurer,
        step: InlineMeasureStep
    )

    /**
     * Количество строк, которое может занимать компонент.
     */
    fun getLineCount(): Int

    /**
     * Внутренние отступы компонента.
     */
    fun computeBounds(): LineBounds
}