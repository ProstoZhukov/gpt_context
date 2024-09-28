package ru.tensor.sbis.widget_player.widget.text

import android.text.*
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutCreator
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutFactory

/**
 * @author am.boldinov
 */
internal object FormattedTextLayoutFactory : LayoutFactory {

    override fun create(
        text: CharSequence,
        paint: TextPaint,
        width: Int,
        alignment: Layout.Alignment,
        textLength: Int,
        spacingMulti: Float,
        spacingAdd: Float,
        includeFontPad: Boolean,
        maxLines: Int,
        isSingleLine: Boolean,
        breakStrategy: Int,
        hyphenationFrequency: Int,
        ellipsize: TextUtils.TruncateAt?,
        textDir: TextDirectionHeuristic,
        boring: BoringLayout.Metrics?,
        boringLayout: BoringLayout?,
        leftIndents: IntArray?,
        rightIndents: IntArray?
    ): Layout {
        return FormattedLayout(LayoutCreator.createStaticLayout(
            text,
            paint,
            width,
            alignment,
            textLength,
            spacingMulti,
            spacingAdd,
            includeFontPad,
            maxLines,
            breakStrategy,
            hyphenationFrequency,
            ellipsize,
            textDir,
            leftIndents,
            rightIndents
        ), leftIndents, rightIndents)
    }
}