package ru.tensor.sbis.widget_player.widget.codeblock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.layout.HorizontalScrollLayout

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class CodeBlockView(
    context: Context,
    private val options: CodeBlockOptions
) : HorizontalScrollLayout(context) {

    private val padding = options.padding.getValuePx(context)

    init {
        clipToPadding = false
        updatePadding(left = padding, right = padding)
        background = getBackgroundDrawable()
    }

    private fun getBackgroundDrawable() = GradientDrawable().apply {
        setColor(options.backgroundColor.getValue(context))
        cornerRadius = options.borderRadius.getValue(context)
        setStroke(
            options.borderThickness.getValuePx(context),
            options.borderColor.getValue(context)
        )
    }

}